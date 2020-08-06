package com.charlag.tuta.user

import android.util.Log
import com.charlag.tuta.*
import com.charlag.tuta.di.AppComponent
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.IdTuple
import com.charlag.tuta.entities.sys.User
import io.ktor.client.features.ClientRequestException
import kotlinx.coroutines.*

interface LoginController {
    val userComponent: UserComponent?

    suspend fun createSession(
        mailAddress: String,
        passphrase: String,
        deviceKey: ByteArray,
        secondFactorCallback: SecondFactorCallback
    )

    suspend fun resumeSession(
        mailAddress: String,
        userId: Id,
        accessToken: String,
        encPassphrase: ByteArray,
        deviceKey: ByteArray
    )

    suspend fun submitTOTPCode(sessionId: IdTuple, code: Long)

    suspend fun switchAccount(id: Id)

    fun getLastCredentials(): Credentials?
    fun getAllCredentials(): List<Credentials>
    suspend fun logout()
}

class RealLoginController(
    private val loginFacade: LoginFacade,
    private val sessionStore: SessionStore,
    private val appComponent: AppComponent,
    private val cryptor: Cryptor

) : LoginController {
    private var sessionData = CompletableDeferred<SessionData>()
    private var deviceKey: ByteArray? = null
    override var userComponent: UserComponent? = null
        private set

    override fun getLastCredentials(): Credentials? {
        return sessionStore.lastUser?.let { sessionStore.loadSessionData(it) }
    }

    override fun getAllCredentials(): List<Credentials> = sessionStore.loadAllCredentials()

    override suspend fun switchAccount(id: Id) {
        val deviceKey =
            checkNotNull(this.deviceKey) { "Can't switch account without logging in first" }
        val newCredentials =
            sessionStore.loadSessionData(id) ?: error("No credentials saved for $id")
        this.userComponent?.let { cancelSession(it) }
        resumeSession(
            newCredentials.mailAddress, id, newCredentials.accessToken, newCredentials.encPassword,
            deviceKey
        )
    }

    override suspend fun createSession(
        mailAddress: String,
        passphrase: String,
        deviceKey: ByteArray,
        secondFactorCallback: SecondFactorCallback
    ) {
        // This is back-and-forth because we need to load user using authenticated API so we create
        // the component here first to get such an API
        val (userId, passpharseKey, accessToken) =
            loginFacade.createSession(mailAddress, passphrase, secondFactorCallback)
        this.userComponent?.let { cancelSession(it) }
        val userComponent = makeUserComponent(userId, passphrase)
        init(userComponent, accessToken)
        val user = userComponent.api().loadElementEntity<User>(userId)
        val userGroupKey =
            loginFacade.getUserGroupKey(user, passpharseKey)
        sessionStore.lastUser = userId
        initSession(SessionData(user, accessToken, userGroupKey), userComponent)

        val encPassphrase = cryptor.encryptString(passphrase, deviceKey)
        this.deviceKey = deviceKey
        sessionStore.saveSessionData(Credentials(userId, accessToken, encPassphrase, mailAddress))
    }

    override suspend fun resumeSession(
        mailAddress: String,
        userId: Id,
        accessToken: String,
        encPassphrase: ByteArray,
        deviceKey: ByteArray
    ) {
        val passphrase = bytesToString(cryptor.aesDecrypt(encPassphrase, deviceKey, true).data)
        // This is back-and-forth because we need to load user using authenticated API so we create
        // the component here first to get such an API
        val userComponent = makeUserComponent(userId, passphrase)
        init(userComponent, accessToken)
        sessionStore.lastUser = userId
        this.deviceKey = deviceKey

        GlobalScope.launch {
            try {
                val passphraseKey = loginFacade.resumeSession(mailAddress, passphrase)
                val user = userComponent.api().loadElementEntity<User>(userId)
                val userGroupKey = loginFacade.getUserGroupKey(user, passphraseKey)

                initSession(SessionData(user, accessToken, userGroupKey), userComponent)
            } catch (e: Exception) {
                Log.e("LoginController", "Failed to log in", e)
//                sessionData.completeExceptionally(e)
            }
        }
    }

    override suspend fun submitTOTPCode(sessionId: IdTuple, code: Long) {
        loginFacade.submitTOTPLogin(sessionId, code)
    }


    override suspend fun logout() {
        val userId: Id
        checkNotNull(userComponent).apply {
            userId = userController().userId
            try {
                pushNotificationsManger().deletePushIdentifier()
            } catch (e: Exception) {
                Log.w(TAG, "Failed to delete push identifier on logout $e")
            }
            sessionStore.lastUser = sessionStore.loadAllCredentials()
                .firstOrNull { it.userId != userId }?.userId

            try {
                groupKeysCache().accessToken?.let {
                    // We need to have loginFacade with accessToken (even though we pass it)
                    loginFacade().deleteSession(it)
                }
            } catch (e: ClientRequestException) {

                Log.w(TAG, "Error during logout", e)
            }

            cancelSession(this)

            withContext(Dispatchers.IO) {
                db().clearAllTables()
                db().close()
            }
        }
        sessionStore.removeSessionData(userId)

    }

    private fun cancelSession(userComponent: UserComponent) {
        userComponent.apply {
            entityEventListener().stop()
            userController().loggedInScope.cancel("Logout")
        }
        this.userComponent = null
        this.sessionData = CompletableDeferred()
    }

    private fun makeUserComponent(
        userId: Id,
        passphrase: String
    ): UserComponent {
        return appComponent.userComponent(
            UserModule(
                userId,
                sessionData,
                passphrase
            )
        )
    }

    private fun initSession(sessionData: SessionData, userComponent: UserComponent) {
        // important: first add key, then notify about loggin in
        userComponent.groupKeysCache().setSessionData(sessionData)
        this.sessionData.complete(sessionData)
        userComponent.entityEventListener().start()
    }

    private fun init(userComponent: UserComponent, accessToken: String) {
        this.userComponent = userComponent
        userComponent.groupKeysCache().setAccessToken(accessToken)
        userComponent.pushNotificationsManger().register()
    }

    companion object {
        private const val TAG = "LoginC"
    }
}