package com.charlag.tuta

import android.util.Log
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging

object DependencyDump {
    var credentials: Credentials? = null
    val cryptor = Cryptor()
    val groupKeysCache = GroupKeysCache(cryptor)
    val httpClient = makeHttpClient {
        this.install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("HTTP", message)
                }
            }
        }
    }
    val compressor = Compressor()

    val api = API(
        httpClient, "https://mail.tutanota.com/rest/", cryptor,
        typemodelMap,
        compressor,
        groupKeysCache,
        accessToken = null
    )
    val loginFacade = LoginFacade(cryptor, api, groupKeysCache)
    val mailFacade = MailFacade(api, cryptor)
}