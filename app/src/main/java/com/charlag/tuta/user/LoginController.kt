package com.charlag.tuta.user

import android.os.Process
import android.util.Log
import com.charlag.tuta.LoginFacade
import com.charlag.tuta.SecondFactorCallback
import com.charlag.tuta.SessionData
import com.charlag.tuta.di.AppComponent
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.sys.User
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface LoginController {
    val userComponent: UserComponent?

    suspend fun createSession(
        mailAddress: String,
        passphrase: String,
        encPassphrase: ByteArray,
        secondFactorCallback: SecondFactorCallback
    )

    suspend fun resumeSession(
        mailAddress: String,
        userId: Id,
        accessToken: String,
        passphrase: String
    )

    suspend fun submitTOTPCode(sessionId: IdTuple, code: Long)

    fun switchAccount(id: String)

    fun getCredentials(): Credentials?
    suspend fun logout()
}

class RealLoginController(
    private val loginFacade: LoginFacade,
    private val sessionStore: SessionStore,
    private val appComponent: AppComponent
) : LoginController {

    override var userComponent: UserComponent? = null
        private set

    private var sessionData = CompletableDeferred<SessionData>()

    override fun getCredentials(): Credentials? {
        return sessionStore.lastUser?.let { sessionStore.loadSessionData(it) }
    }

    override fun switchAccount(id: String) {
        TODO("not implemented")
    }

    override suspend fun createSession(
        mailAddress: String,
        passphrase: String,
        encPassphrase: ByteArray,
        secondFactorCallback: SecondFactorCallback
    ) {
        // This is back-and-forth because we need to load user using authenticated API so we create
        // the component here first to get such an API
        val (userId, passpharseKey, accessToken) =
            loginFacade.createSession(mailAddress, passphrase, secondFactorCallback)
        val userComponent = makeUserComponent(userId, passphrase)
        init(userComponent, accessToken)

        val user = userComponent.api().loadElementEntity<User>(userId)
        val userGroupKey =
            loginFacade.getUserGroupKey(user, passpharseKey)
        sessionStore.saveSessionData(Credentials(userId, accessToken, encPassphrase, mailAddress))
        sessionStore.lastUser = userId
        initSession(SessionData(user, accessToken, userGroupKey), userComponent)
    }

    override suspend fun resumeSession(
        mailAddress: String,
        userId: Id,
        accessToken: String,
        passphrase: String
    ) {
        // This is back-and-forth because we need to load user using authenticated API so we create
        // the component here first to get such an API
        val userComponent = makeUserComponent(userId, passphrase)
        init(userComponent, accessToken)
        sessionStore.lastUser = userId

        GlobalScope.launch {
            try {
                val passphraseKey = loginFacade.resumeSession(mailAddress, passphrase)
                val user = userComponent.api().loadElementEntity<User>(userId)
                val userGroupKey = loginFacade.getUserGroupKey(user, passphraseKey)

                initSession(SessionData(user, accessToken, userGroupKey), userComponent)
            } catch (e: Exception) {
                Log.e("LoginController", "Failed to log in")
                Process.killProcess(Process.myPid())
            }
        }
    }

    override suspend fun submitTOTPCode(sessionId: IdTuple, code: Long) {
        loginFacade.submitTOTPLogin(sessionId, code)
    }


    override suspend fun logout() {
        val loadedSessionData = sessionData.await()
        loginFacade.deleteSession(loadedSessionData.accessToken)
        sessionStore.removeSessionData(loadedSessionData.user._id)
        // TODO: clear DB here too
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
        this.sessionData.complete(sessionData)
        userComponent.groupKeysCache().stage2(sessionData)
    }

    private fun init(userComponent: UserComponent, accessToken: String) {
        this.userComponent = userComponent
        userComponent.groupKeysCache().stage1(accessToken)
        userComponent.pushNotificationsManger().register()
    }
}