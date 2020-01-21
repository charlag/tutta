package com.charlag.tuta

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailField = findViewById<TextView>(R.id.email_field)
        val passwordField = findViewById<TextView>(R.id.password_field)
        val loginButton = findViewById<Button>(R.id.login_btn)
        val statusLabel = findViewById<TextView>(R.id.status_tv)

        loginButton.setOnClickListener {
            val mailAddress = emailField.text.toString()
            val password = passwordField.text.toString()

            statusLabel.text = "Logging in"
            GlobalScope.launch {
                try {
                    val sessionData = withContext(Dispatchers.IO) {
                        val (user, _, accessToken) = DependencyDump.loginFacade.createSession(
                            mailAddress,
                            password
                        )
                        val userId = user._id.asString()
                        PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
                            .edit()
                            .putString("userId", userId)
                            .putString("accessToken", accessToken)
                            .putString("password", password)
                            .putString("mailAddress", mailAddress)
                            .apply()
                        DependencyDump.credentials =
                            Credentials(userId, accessToken, password, mailAddress)
                    }

                    withContext(Dispatchers.Main) {
                        statusLabel.text = "Success! $sessionData"
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
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
