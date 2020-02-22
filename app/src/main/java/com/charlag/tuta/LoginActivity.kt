package com.charlag.tuta

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.charlag.tuta.entities.IdTuple
import com.charlag.tuta.login.AuthResult
import com.charlag.tuta.login.Authenticator
import com.charlag.tuta.user.Credentials
import com.charlag.tuta.user.LoginController
import com.charlag.tuta.user.SessionStore
import dagger.android.support.DaggerAppCompatActivity
import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class LoginActivity : DaggerAppCompatActivity() {

    @Inject
    internal lateinit var loginController: LoginController
    @Inject
    internal lateinit var sessionStore: SessionStore
    @Inject
    internal lateinit var authenticator: Authenticator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_login)

        if (intent.action == NEW_ACCOUNT_ACTION) {
            backButton.isVisible = true
            backButton.setOnClickListener {
                finish()
            }
        } else {
            val sessionData = loginController.getLastCredentials()
            if (sessionData != null) {
                emailField.setText(sessionData.mailAddress)
                biometricButton.visibility = View.VISIBLE
                biometricButton.setOnClickListener {
                    loginWithSaved(sessionData)
                }
                loginWithSaved(sessionData)
            }
        }

        loginButton.setOnClickListener {
            loginWithNewAccount()
        }
    }

    private fun loginWithNewAccount() {
        val mailAddress = emailField.text.toString()
        val password = passwordField.text.toString()

        enableFields(false)
        passwordField.error = null
        statusLabel.text = "Logging in"
        lifecycleScope.launch {
            try {
                val deviceKey =
                    when (val authResult = authenticator.getDeviceKey(this@LoginActivity)) {
                        is AuthResult.Success -> authResult.data
                        else -> {
                            enableFields(true)
                            return@launch
                        }
                    }
                withContext(Dispatchers.IO) {
                    loginController.createSession(
                        mailAddress,
                        password,
                        deviceKey,
                        this@LoginActivity::handleTOTP
                    )
                }
                withContext(Dispatchers.Main) {
                    statusLabel.text = "Success!"
                    goToMain()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("Main", "ooops", e)
                    enableFields(true)
                    if (e.isUnauthorized()) {
                        enableFields(true)
                        passwordField.error = "Not authorized"
                    }
                    statusLabel.text = "Error ${e.message}"
                }
            }
        }
    }

    private fun enableFields(status: Boolean) {
        emailField.isEnabled = status
        passwordField.isEnabled = status
        loginButton.isEnabled = status
    }

    private fun handleTOTP(sessionId: IdTuple) {
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

    private fun goToMain() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
            if (this@LoginActivity.intent.action == NEW_ACCOUNT_ACTION) {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
        startActivity(intent)
        finish()
    }

    private fun loginWithSaved(credentials: Credentials) {
        enableFields(false)
        passwordField.error = null
        lifecycleScope.launch {
            try {
                val deviceKey =
                    when (val authResult = authenticator.getDeviceKey(this@LoginActivity)) {
                        is AuthResult.Success -> authResult.data
                        else -> {
                            enableFields(true)
                            return@launch
                        }
                    }
                try {
                    loginController.resumeSession(
                        credentials.mailAddress,
                        credentials.userId,
                        credentials.accessToken,
                        credentials.encPassword,
                        deviceKey
                    )
                    goToMain()
                } catch (e: IOException) {
                    val msg = "Failed to log in because of IO $e"
                    withContext(Dispatchers.Main) {
                        statusLabel.text = msg
                    }
                    Log.w("Main", msg)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d("Mail", "Failed to log in", e)
                    enableFields(true)
                    statusLabel.text = "Failed to log in ${e.message}"
                }
            }
        }
    }

    private fun Exception.isUnauthorized() = this is ClientRequestException &&
            this.response.status == HttpStatusCode.Unauthorized

    companion object {
        fun normalIntent(context: Context) = Intent(context, LoginActivity::class.java)

        fun newAccountIntent(context: Context) = Intent(context, LoginActivity::class.java).apply {
            action = NEW_ACCOUNT_ACTION
        }

        private const val NEW_ACCOUNT_ACTION = "com.charlag.tuta.newlogin"
    }
}

/**
 * Amsterdam biker: on hollander bike
 * Hamburg biker: some fancy sport bike
 * Hannover biker: some guy in sport clothes on trike with a wagon lol
 */
