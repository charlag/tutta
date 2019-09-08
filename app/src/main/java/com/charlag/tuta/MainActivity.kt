package com.charlag.tuta

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.charlag.tuta.entities.Id
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emailField = findViewById<TextView>(R.id.email_field)
        val passwordField = findViewById<TextView>(R.id.password_field)
        val loginButton = findViewById<Button>(R.id.login_btn)
        val statusLabel = findViewById<TextView>(R.id.status_tv)

        // auth verifier "hl2y-Y3k6KRDDa-ly1lO8p29LmeO62E4AxaJu3DaXz8"

        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            statusLabel.text = "Logging in"
            GlobalScope.launch {
                try {
                    val sessionData = withContext(Dispatchers.IO) {
                        val groupKeysCache = mutableMapOf<Id, ByteArray>()


                        val httpClient = makeHttpClient {
                            this.install(Logging) {
                                logger = object : Logger {
                                    override fun log(message: String) {
                                        Log.d("HTTP", message)
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
                        statusLabel.text = "Success! $sessionData"
                    }
                } catch (e: Exception) {
                    Log.e("Main", "ooops", e)
                    withContext(Dispatchers.Main) {
                        statusLabel.text = "Error $e"
                    }
                }
            }
        }

    }
}

/**
 * Amsterdam biker: on hollander bike
 * Hamburg biker: some fancy sport bike
 * Hannover biker: some guy in sport clothes on trike with a wagon lol
 */
