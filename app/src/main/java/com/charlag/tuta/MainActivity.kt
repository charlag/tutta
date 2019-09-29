package com.charlag.tuta

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.User
import com.charlag.tuta.entities.tutanota.MailboxGroupRoot
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

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
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
            typemodelMap, groupKeysCache,
            accessToken = null
        )
        val loginFacade = LoginFacade(cryptor, api, groupKeysCache)

        val userId = prefs.getString("userId", null)
        val accessToken = prefs.getString("accessToken", null)
        val password = prefs.getString("password", null)
        val mailAddress = prefs.getString("mailAddress", null)


        if (userId != null && accessToken != null && password != null && mailAddress != null) {
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    loginFacade.resumeSession(
                        mailAddress,
                        GeneratedId(userId),
                        accessToken,
                        password
                    )
                    loadMailBoxGroupRoot(loginFacade, api)
                }
            }
        }

        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            statusLabel.text = "Logging in"
            GlobalScope.launch {
                try {
                    val sessionData = withContext(Dispatchers.IO) {
                        val (user, _, accessToken) = loginFacade.createSession(
                            email,
                            password
                        )
                        prefs.edit()
                            .putString("userId", user._id.asString())
                            .putString("accessToken", accessToken)
                            .putString("password", password)
                            .putString("mailAddress", email)
                            .apply()
                    }

                    loadMailBoxGroupRoot(loginFacade, api)

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

    private suspend fun loadMailBoxGroupRoot(
        loginFacade: LoginFacade,
        api: API
    ) {
        val mailMembership = loginFacade.user!!.memberships.filter {
            it.groupType == GroupType.Mail.value
        }.first()
        val mailboxGroupRoot: MailboxGroupRoot =
            api.loadElementEntity(mailMembership.group)
        println(mailboxGroupRoot)
    }
}

/**
 * Amsterdam biker: on hollander bike
 * Hamburg biker: some fancy sport bike
 * Hannover biker: some guy in sport clothes on trike with a wagon lol
 */
