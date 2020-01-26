package com.charlag.tuta

import android.util.Log
import com.charlag.tuta.data.AppDatabase
import com.charlag.tuta.events.EntityEventListener
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.WebSockets
import io.ktor.util.KtorExperimentalAPI

object DependencyDump {
    var credentials: Credentials? = null
    val cryptor = Cryptor()
    val groupKeysCache = GroupKeysCache(cryptor)
    @UseExperimental(KtorExperimentalAPI::class)
    val httpClient = makeHttpClient {
        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("HTTP", message)
                }
            }
        }
        install(WebSockets)
    }
    val compressor = Compressor()

    val api = API(
        httpClient, "https://mail.tutanota.com/rest/",
        cryptor,
        typemodelMap,
        compressor,
        groupKeysCache,
        accessToken = null,
        wsUrl = "wss://mail.tutanota.com/event"
    )
    val loginFacade = LoginFacade(cryptor, api, groupKeysCache)
    val mailFacade = MailFacade(api, cryptor)
    lateinit var db: AppDatabase
    val eventListener = EntityEventListener(loginFacade, api, { db })
}