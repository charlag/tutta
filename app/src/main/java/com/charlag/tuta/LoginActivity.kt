package com.charlag.tuta

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.charlag.tuta.user.Credentials
import com.charlag.tuta.user.LoginController
import dagger.android.support.DaggerAppCompatActivity
import io.ktor.client.features.ClientRequestException
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.crypto.Cipher
import javax.inject.Inject

class LoginActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var loginController: LoginController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_login)

        val sessionData = loginController.getCredentials()

        if (sessionData != null) {
            emailField.setText(sessionData.mailAddress)
            biometricButton.visibility = View.VISIBLE
            biometricButton.setOnClickListener {
                loginWithSaved(sessionData)
            }
            loginWithSaved(sessionData)
        }

        loginButton.setOnClickListener {
            val mailAddress = emailField.text.toString()
            val password = passwordField.text.toString()

            statusLabel.text = "Logging in"
            lifecycleScope.launch {
                try {
                    CredentialsManager()
                        .register()
                    val cipher = getCipher(null) ?: return@launch
                    val encryptedPassword = cipher.doFinal(password.toByteArray())
                    val encryptedPasswordWithIv = cipher.iv + encryptedPassword

                    withContext(Dispatchers.IO) {
                        loginController.createSession(
                            mailAddress,
                            password,
                            encryptedPasswordWithIv
                        ) { sessionId ->
                            loginButton.post {
                                val field = EditText(this@LoginActivity).apply {
                                    hint = "TOTP code"
                                }
                                val dialog = AlertDialog.Builder(this@LoginActivity)
                                    .setView(field)
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show()
                                val okButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                                okButton.setOnClickListener {
                                    val totpCode = field.text.toString().toLong()
                                    lifecycleScope.launch {
                                        try {
                                            loginController.submitTOTPCode(
                                                sessionId,
                                                totpCode
                                            )
                                            dialog.dismiss()
                                        } catch (e: ClientRequestException) {
                                            Log.d("Login", "TOTP request failed $e")
                                            Toast.makeText(
                                                this@LoginActivity,
                                                "TOTP didn't match",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                                field.doAfterTextChanged {
                                    okButton.isEnabled = field.text.length >= 6 &&
                                            field.text.toString().toLongOrNull() != null
                                }
                            }

                        }
                    }

                    withContext(Dispatchers.Main) {
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

    private fun loginWithSaved(credentials: Credentials) {
        lifecycleScope.launch {
            try {
                val passwordWithIvBytes = credentials.encPassword
                val iv = passwordWithIvBytes.copyOfRange(0, 16)
                val encPasswordBytes =
                    passwordWithIvBytes.copyOfRange(16, passwordWithIvBytes.size)
                val cipher = getCipher(iv)
                if (cipher == null) {
                    Log.d("Main", "Failed to get login cipher")
                    return@launch
                }
                val password = bytesToString(cipher.doFinal(encPasswordBytes))

                try {
                    loginController.resumeSession(
                        credentials.mailAddress,
                        credentials.userId,
                        credentials.accessToken,
                        password
                    )
                    goToMain()
                } catch (e: IOException) {
                    val msg = "Failed to log in because of IO $e"
                    withContext(Dispatchers.Main) {
                        statusLabel.text = msg
                    }
                    Log.w("Main", msg)
                }
            } catch (e: java.lang.Exception) {
                Log.d("Mail", "Failed to log in", e)
                statusLabel.text = "Failed to log in $e"
            }
        }
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
                    Log.d("Login", "Authentication failed")
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
