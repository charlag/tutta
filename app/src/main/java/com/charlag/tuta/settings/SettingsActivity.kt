package com.charlag.tuta.settings

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.charlag.tuta.DependencyDump
import com.charlag.tuta.LoginActivity
import com.charlag.tuta.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsActivity : AppCompatActivity(R.layout.activity_settings) {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

    }

    override fun onStart() {
        super.onStart()
        logoutButton.setOnClickListener {
            val credentials = DependencyDump.credentials
            lifecycleScope.launch {
                if (credentials != null) {
                    DependencyDump.loginFacade.deleteSession(credentials.accessToken)
                    DependencyDump.credentials = null
                    PreferenceManager.getDefaultSharedPreferences(this@SettingsActivity)
                        .edit()
                        .clear()
                        .apply()
                }
                startActivity(
                    Intent(this@SettingsActivity, LoginActivity::class.java).setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                )
                finish()
            }
        }

        clearButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Are you sure you want to delete mail data?")
                .setPositiveButton(android.R.string.yes) { dialog, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        DependencyDump.db.mailDao().deleteAllMails()
                        DependencyDump.db.mailDao().deleteAllMailFolders()
                        DependencyDump.db.mailDao().deleteAllMailBodies()
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            "Deleted",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.no) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}