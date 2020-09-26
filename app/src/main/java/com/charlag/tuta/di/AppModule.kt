package com.charlag.tuta.di

import android.content.Context
import android.util.Log
import com.charlag.tuta.*
import com.charlag.tuta.login.Authenticator
import com.charlag.tuta.login.RealAuthenticator
import com.charlag.tuta.network.*
import com.charlag.tuta.network.mapping.InstanceMapper
import com.charlag.tuta.notifications.AndroidKeyStoreFacade
import com.charlag.tuta.notifications.data.NotificationDatabase
import com.charlag.tuta.user.LoginController
import com.charlag.tuta.user.RealLoginController
import com.charlag.tuta.user.SessionStore
import com.charlag.tuta.user.SharedPrefSessionStore
import dagger.Module
import dagger.Provides
import io.ktor.client.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class RestPath

@Qualifier
annotation class WsPath

@Qualifier
annotation class SSEPath

@Module
internal object AppModule {
    private const val REST_PATH = "https://mail.tutanota.com/rest/"
    private const val WS_PATH = "wss://mail.tutanota.com/event/"
    private const val SSE_PATH = "https://mail.tutanota.com/"

    @Provides
    @RestPath
    fun restPath() = REST_PATH

    @Provides
    @WsPath
    fun wsPath() = WS_PATH

    @Provides
    @SSEPath
    fun ssePath() = SSE_PATH

    @Provides
    fun preferencesFacade(context: Context): PreferenceFacade =
        SharedPreferencesPreferenceFacade(context)

    @Provides
    @Singleton
    fun cryptor(): Cryptor = Cryptor()

    @Provides
    @Singleton
    fun instanceMapper(cryptor: Cryptor, compressor: Compressor): InstanceMapper {
        return InstanceMapper(
            cryptor,
            compressor,
            typemodelMap
        )
    }

    @Provides
    @Singleton
    fun compressor() = Compressor()

    @Provides
    @Singleton
    @NonAuthenticated
    fun providesApi(
        httpClient: HttpClient,
        cryptor: Cryptor,
        instanceMapper: InstanceMapper,
        @NonAuthenticated keyResolver: SessionKeyResolver,
        @NonAuthenticated sessionDataProvider: SessionDataProvider
    ): API =
        API(
            httpClient,
            REST_PATH,
            cryptor,
            instanceMapper,
            sessionDataProvider,
            keyResolver,
            WS_PATH
        )

    @UseExperimental(KtorExperimentalAPI::class)
    @Provides
    fun providesHttpClient(): HttpClient {
        return makeHttpClient {
            install(Logging) {
                level = LogLevel.INFO
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("HTTP", message)
                    }
                }
            }
            install(WebSockets)
        }
    }

    @Provides
    @Singleton
    fun loginFacade(cryptor: Cryptor, @NonAuthenticated api: API): LoginFacade =
        LoginFacade(cryptor, api)

    @Provides
    @Singleton
    @NonAuthenticated
    fun sessionKeyResolver(
        cryptor: Cryptor,
        @NonAuthenticated sessionDataProvider: SessionDataProvider
    ): SessionKeyResolver =
        SessionKeyResolver(cryptor, sessionDataProvider)

    @Provides
    @Singleton
    @NonAuthenticated
    fun groupKeysCache(cryptor: Cryptor): SessionDataProvider = UserSessionDataProvider(cryptor)

    @Provides
    @Singleton
    fun loginController(
        loginFacade: LoginFacade,
        sessionStore: SessionStore,
        appComponent: AppComponent,
        cryptor: Cryptor
    ): LoginController {
        Log.d("App", "LoginController")
        return RealLoginController(loginFacade, sessionStore, appComponent, cryptor)
    }

    @Provides
    @Singleton
    fun providesJson(): Json = Json {  }

    @Provides
    @Singleton
    fun sessionStore(context: Context, json: Json): SessionStore =
        SharedPrefSessionStore(context, json)

    @Provides
    @Singleton
    fun notificationsDabtabse(context: Context): NotificationDatabase =
        NotificationDatabase.getDatabase(context, false)

    @Provides
    fun authenticator(
        sessionStore: SessionStore,
        keyStoreFacade: AndroidKeyStoreFacade,
        cryptor: Cryptor
    ): Authenticator =
        RealAuthenticator(sessionStore, keyStoreFacade, cryptor)
}