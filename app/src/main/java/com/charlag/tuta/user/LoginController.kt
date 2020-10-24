package com.charlag.tuta.user

import android.util.Log
import com.charlag.tuta.*
import com.charlag.tuta.di.AppComponent
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.IdTuple
import com.charlag.tuta.entities.sys.User
import com.charlag.tuta.network.SessionData
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
    private val appComponent: AppComponent
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

        // Stage 1: get user id/accessToken
        val createSessionResult =
            loginFacade.createSession(
                mailAddress,
                passphrase,
                SessionType.PERSISTENT,
                secondFactorCallback
            )
        // Taking it apart manually so that we don't rely on order and don't misuse something
        val userId = createSessionResult.userId
        val accessToken = createSessionResult.accessToken
        val passphraseKey = createSessionResult.passphraseKey
        val encryptedPassword = createSessionResult.encryptedPassword

        this.userComponent?.let { cancelSession(it) }
        val userComponent = makeUserComponent(userId, deviceKey)
        init(userComponent, accessToken)
        // Stage 2: get user
        val user = userComponent.api().loadElementEntity<User>(userId)
        val userGroupKey =
            loginFacade.getUserGroupKey(user, passphraseKey)
        sessionStore.lastUser = userId
        // stage 3: set user and load everything else
        initSession(SessionData(user, accessToken, userGroupKey), userComponent)

        this.deviceKey = deviceKey
        sessionStore.saveSessionData(
            Credentials(
                userId,
                accessToken,
                encryptedPassword!!,
                mailAddress
            )
        )
    }

    override suspend fun resumeSession(
        mailAddress: String,
        userId: Id,
        accessToken: String,
        encPassphrase: ByteArray,
        deviceKey: ByteArray
    ) {
        // This is back-and-forth because we need to load user using authenticated API so we create
        // the component here first to get such an API
        val userComponent = makeUserComponent(userId, deviceKey)
        init(userComponent, accessToken)
        sessionStore.lastUser = userId
        this.deviceKey = deviceKey

        GlobalScope.launch {
            try {
                // We have initialized userComponent with accessToken and we should use loginFacade
                // from it so that we can load session with accessToken
                val resumeData = userComponent.loginFacade().resumeSession(mailAddress, accessToken, encPassphrase)
                val user = userComponent.api().loadElementEntity<User>(userId)
                val userGroupKey = loginFacade.getUserGroupKey(user, resumeData.passphraseKey)

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
        dbPassphrase: ByteArray
    ): UserComponent {
        return appComponent.userComponent(
            UserModule(
                userId,
                sessionData,
                dbPassphrase
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