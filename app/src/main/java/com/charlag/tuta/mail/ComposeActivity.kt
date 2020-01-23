package com.charlag.tuta.mail

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.charlag.tuta.ConversationType
import com.charlag.tuta.DependencyDump
import com.charlag.tuta.R
import com.charlag.tuta.RecipientInfo
import kotlinx.android.synthetic.main.activity_compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class ComposeActivity : AppCompatActivity() {
    val mailFacade = DependencyDump.mailFacade
    val loginFacade = DependencyDump.loginFacade

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val activity = this
        menu.add("Send")
            .setIcon(R.drawable.ic_send_black_24dp)
            .setOnMenuItemClickListener {
                Toast.makeText(this, "Sending", Toast.LENGTH_SHORT).show()
                GlobalScope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            mailFacade.createDraft(
                                user = loginFacade.user!!,
                                subject = subjectField.text.toString(),
                                body = contentField.text.toString(),
                                senderAddress = "bedfortest@tutanota.de",
                                senderName = "Bedfortest",
                                toRecipients = listOf(
                                    RecipientInfo(
                                        name = "",
                                        mailAddress = toField.text.toString()
                                    )
                                ),
                                ccRecipients = listOf(),
                                bccRecipients = listOf(),
                                conversationType = ConversationType.NEW,
                                previousMessageId = null,
                                confidential = true,
                                replyTos = listOf()
                            )
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(activity, "Sent", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.e("Compose", "error", e)
                            Toast.makeText(activity, "Error $e", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                true
            }
            .setVisible(true)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return super.onCreateOptionsMenu(menu)
    }
}