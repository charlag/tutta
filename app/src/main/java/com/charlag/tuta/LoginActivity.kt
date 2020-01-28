package com.charlag.tuta

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import javax.crypto.Cipher

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
            lifecycleScope.launch {
                try {
                    val (user, _, accessToken) = withContext(Dispatchers.IO) {
                        DependencyDump.loginFacade.createSession(
                            mailAddress,
                            password
                        )
                    }

                    withContext(Dispatchers.Main) {
                        val userId = user._id.asString()
                        CredentialsManager()
                            .register()
                        val cipher = getCipher() ?: return@withContext
                        val encryptedPassword = cipher.doFinal(password.toByteArray())
                        val encryptedPasswordWithIv = cipher.iv + encryptedPassword
                        PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
                            .edit()
                            .putString("userId", userId)
                            .putString("accessToken", accessToken)
                            .putString("password", encryptedPasswordWithIv.toBase64())
                            .putString("mailAddress", mailAddress)
                            .apply()
                        DependencyDump.credentials =
                            Credentials(userId, accessToken, password, mailAddress)
                        statusLabel.text = "Success!"
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

    suspend fun getCipher(): Cipher? {
        val deferred = CompletableDeferred<Cipher?>()
        val prompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    Toast.makeText(
                        applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    ).show()
                    deferred.complete(null)
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    Toast.makeText(
                        applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT
                    ).show()
                    deferred.complete(result.cryptoObject?.cipher)
                }

                override fun onAuthenticationFailed() {
                    Toast.makeText(
                        applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    deferred.complete(null)
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login")
            .setNegativeButtonText("Use account password")
            .build()
        prompt.authenticate(
            promptInfo,
            BiometricPrompt.CryptoObject(CredentialsManager().prepareCipher(iv = null))
        )
        return deferred.await()
    }
}

/**
 * Amsterdam biker: on hollander bike
 * Hamburg biker: some fancy sport bike
 * Hannover biker: some guy in sport clothes on trike with a wagon lol
 */
