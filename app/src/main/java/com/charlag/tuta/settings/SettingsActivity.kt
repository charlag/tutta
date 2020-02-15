package com.charlag.tuta.settings

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.charlag.tuta.AuthenticatedActivity
import com.charlag.tuta.LoginActivity
import com.charlag.tuta.R
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.launch


class SettingsActivity : AuthenticatedActivity(R.layout.activity_settings) {

    override fun onStart() {
        super.onStart()
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
    }
}