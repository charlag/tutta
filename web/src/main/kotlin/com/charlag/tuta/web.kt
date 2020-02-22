package com.charlag.tuta.web

import com.charlag.tuta.*
import com.charlag.tuta.network.*
import com.charlag.tuta.network.mapping.InstanceMapper
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
                    UserSessionDataProvider(cryptor)
                val url =
                    if (platformName() == "JS") "http://localhost:9000/rest" else "https://mail.tutanota.com/rest/"
                val compressor = Compressor()
                val instanceMapper =
                    InstanceMapper(
                        cryptor,
                        compressor,
                        typemodelMap
                    )
                val keyResolver = SessionKeyResolver(cryptor, groupKeysCache)
                val api = API(
                    httpClient, url, cryptor,
                    instanceMapper, groupKeysCache,
                    keyResolver,
                    wsUrl = "wss://mail.tutanota.com/events"
                )
                LoginFacade(cryptor, api).createSession(email, password) {
                    TODO()
                }
            }

            withContext(Dispatchers.Main) {
                println("Success!")
            }
        } catch (e: Exception) {
            println("error $e")
        }
    }
}