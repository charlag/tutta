package com.charlag.tuta.web

import com.charlag.tuta.*
import com.charlag.tuta.entities.Id
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
                val groupKeysCache = mutableMapOf<Id, ByteArray>()


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
                val api = API(
                    httpClient, "https://mail.tutanota.com/rest/", cryptor,
                    typemodelMap, groupKeysCache
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