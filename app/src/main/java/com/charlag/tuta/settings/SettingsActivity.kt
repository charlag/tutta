package com.charlag.tuta.settings

import android.content.Intent
import android.util.Log
import android.view.MenuItem
import androidx.biometric.BiometricManager
import androidx.lifecycle.lifecycleScope
import com.charlag.tuta.AuthenticatedActivity
import com.charlag.tuta.LoginActivity
import com.charlag.tuta.R
import com.charlag.tuta.login.AuthResult
import com.charlag.tuta.login.Authenticator
import com.charlag.tuta.user.SessionStore
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsActivity : AuthenticatedActivity(R.layout.activity_settings) {
    @Inject
    internal lateinit var sessionStore: SessionStore
    @Inject
    internal lateinit var authenticator: Authenticator

    override fun onStart() {
        super.onStart()

        setSupportActionBar(toolbar)

        logoutButton.setOnClickListener {
            lifecycleScope.launch {
                loginController.logout()
                startActivity(
                    Intent(this@SettingsActivity, LoginActivity::class.java).setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                )
                finish()
            }
        }

        updateBiometricsSwitch()
        val biometricManager = BiometricManager.from(this)

        biometricSwitch.setOnClickListener {
            when (biometricManager.canAuthenticate()) {
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                    snack("No biometric hardware available")
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                    snack("Biometric hardware unavailable")
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                    snack("No biometric methods enrolled")
                BiometricManager.BIOMETRIC_SUCCESS ->
                    changeBiometrics(biometricSwitch.isChecked)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun snack(text: String) =
        Snackbar.make(biometricSwitch, text, Snackbar.LENGTH_SHORT).show()

    private fun changeBiometrics(desiredValue: Boolean) {
        lifecycleScope.launch {
            when (authenticator.changeBiometricsPref(this@SettingsActivity, desiredValue)) {
                !is AuthResult.Success -> biometricSwitch.isChecked = !desiredValue
            }
        }
    }

    private fun updateBiometricsSwitch() {
        biometricSwitch.isChecked = sessionStore.usingBiometrics
    }
}