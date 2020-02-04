package com.charlag.tuta.compose

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.charlag.tuta.R
import kotlinx.android.synthetic.main.activity_compose.*
import kotlinx.coroutines.launch

class ComposeActivity : AppCompatActivity() {
    private val viewModel: ComposeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                mutableListOf<String>()
            )
        fromSpinner.adapter = adapter

        viewModel.enabledMailAddresses.observe(this) { mailAddresses ->
            adapter.clear()
            adapter.addAll(mailAddresses)
            adapter.notifyDataSetChanged()
        }

        val onAddRecipient = viewModel::addRecipient
        val onRemoveRecipient = viewModel::removeRecipient
        val autocompleteProvider = viewModel::autocompleteMailAddress

        setupRecipientField(
            toField,
            RecipientField.TO,
            autocompleteProvider,
            onAddRecipient,
            onRemoveRecipient
        )
        setupRecipientField(
            ccField, RecipientField.CC,
            autocompleteProvider,
            onAddRecipient,
            onRemoveRecipient
        )
        setupRecipientField(
            bccField, RecipientField.BCC,
            autocompleteProvider,
            onAddRecipient,
            onRemoveRecipient
        )

        expandRecipientsButton.setOnClickListener {
            expandRecipients()
        }

        viewModel.willBeSentEncrypted.observe(this) {
            encryptionIndicator.isVisible = it
        }
    }

    private fun expandRecipients() {
        ccTextView.isVisible = true
        ccField.isVisible = true
        ccBccSeparator.isVisible = true
        bccTextView.isVisible = true
        bccField.isVisible = true
        bccSubjectSeparator.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("Send")
            .setIcon(R.drawable.ic_send_black_24dp)
            .setOnMenuItemClickListener {
                send()
                true
            }
            .setVisible(true)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return super.onCreateOptionsMenu(menu)
    }

    private fun send() {
        Toast.makeText(this, "Sending….", Toast.LENGTH_SHORT).show()
        val activity = this

        lifecycleScope.launch {
            try {
                val didSend = viewModel.send(
                    subjectField.text.toString(),
                    contentField.text.toString(),
                    fromSpinner.selectedItem as String
                )
                if (didSend) {
                    Toast.makeText(activity, "Sent", Toast.LENGTH_SHORT).show()
                    finish()
                }

            } catch (e: Exception) {
                Log.e("Compose", "error", e)
                Toast.makeText(activity, "Error $e", Toast.LENGTH_SHORT).show()
            }
        }
    }
}