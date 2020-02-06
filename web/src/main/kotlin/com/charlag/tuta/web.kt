package com.charlag.tuta.web

import com.charlag.tuta.*
import com.charlag.tuta.network.API
import com.charlag.tuta.network.GroupKeysCache
import com.charlag.tuta.network.InstanceMapper
import com.charlag.tuta.network.SessionKeyResolver
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun main(args: Array<String>) {
    val email = ""
    val password = ""
    GlobalScope.launch {
        try {
            val sessionData = withContext(Dispatchers.Default) {


                val httpClient = makeHttpClient {
                    this.install(Logging) {
                        logger = object : Logger {
                            override fun log(message: String) {
                                println("HTTP: $message")
                            }
                        }
                        level = LogLevel.ALL
                    }
                }
                val cryptor = Cryptor()
                val groupKeysCache =
                    GroupKeysCache(cryptor)
                val url =
                    if (platformName() == "JS") "http://localhost:9000/rest" else "https://mail.tutanota.com/rest/"
                val compressor = Compressor()
                val instanceMapper = InstanceMapper(cryptor, compressor, typemodelMap)
                val keyResolver = SessionKeyResolver(cryptor, groupKeysCache)
                val api = API(
                    httpClient, url, cryptor,
                    instanceMapper, groupKeysCache,
                    keyResolver,
                    accessToken = null,
                    wsUrl = "wss://mail.tutanota.com/events"
                )
                LoginFacade(cryptor, api, groupKeysCache).createSession(email, password)
            }

            withContext(Dispatchers.Main) {
                println("Success!")
            }
        } catch (e: Exception) {
            println("error $e")
        }
    }
}