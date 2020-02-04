package com.charlag.tuta

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.charlag.tuta.entities.GeneratedId
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*
import java.io.IOException
import javax.crypto.Cipher

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val savedUserId = prefs.getString("userId", null)
        val savedAccessToken = prefs.getString("accessToken", null)
        val encPassword = prefs.getString("password", null)
        val savedMailAddress = prefs.getString("mailAddress", null)


        if (savedUserId != null && savedAccessToken != null &&
            encPassword != null && savedMailAddress != null
        ) {
            emailField.setText(savedMailAddress)
            biometricButton.visibility = View.VISIBLE
            biometricButton.setOnClickListener {
                loginWithSaved(encPassword, savedUserId, savedAccessToken, savedMailAddress)
            }
            loginWithSaved(encPassword, savedUserId, savedAccessToken, savedMailAddress)
        }

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
                        val cipher = getCipher(null) ?: return@withContext
                        val encryptedPassword = cipher.doFinal(password.toByteArray())
                        val encryptedPasswordWithIv = cipher.iv + encryptedPassword
                        igniteApp(password)

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
                        goToMain()
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

    private fun goToMain() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    private fun loginWithSaved(
        encPassword: String,
        savedUserId: String,
        savedAccessToken: String,
        savedMailAddress: String
    ) {
        lifecycleScope.launch {
            try {
                val passwordWithIvBytes = base64ToBytes(encPassword)
                val iv = passwordWithIvBytes.copyOfRange(0, 16)
                val encPasswordBytes =
                    passwordWithIvBytes.copyOfRange(16, passwordWithIvBytes.size)
                val cipher = getCipher(iv)
                if (cipher == null) {
                    Log.d("Main", "Failed to get login cipher")
                    return@launch
                }
                val password = bytesToString(cipher.doFinal(encPasswordBytes))
                igniteApp(password)
                DependencyDump.credentials =
                    Credentials(savedUserId, savedAccessToken, password, savedMailAddress)
                goToMain()
                GlobalScope.launch {
                    try {
                        DependencyDump.loginFacade.resumeSession(
                            savedMailAddress,
                            GeneratedId(savedUserId),
                            savedAccessToken,
                            password
                        )
                    } catch (e: IOException) {
                        val msg = "Failed to log in because of IO $e"
                        statusLabel.text = msg
                        Log.w("Main", msg)
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.d("Mail", "Failed to log in", e)
                statusLabel.text = "Failed to log in $e"
            }
        }
    }

    private fun igniteApp(password: String) {
        // We *probably* should use another password but we would need another level of indirection
        // and that's not what we want probably.
        DependencyDump.ignite(password, applicationContext)
    }

    suspend fun getCipher(iv: ByteArray?): Cipher? {
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
            BiometricPrompt.CryptoObject(CredentialsManager().prepareCipher(iv))
        )
        return deferred.await()
    }
}

/**
 * Amsterdam biker: on hollander bike
 * Hamburg biker: some fancy sport bike
 * Hannover biker: some guy in sport clothes on trike with a wagon lol
 */
