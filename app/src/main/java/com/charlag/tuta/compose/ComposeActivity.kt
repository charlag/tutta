package com.charlag.tuta.compose

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.provider.OpenableColumns
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.charlag.tuta.R
import com.charlag.tuta.mail.AttachmentAdapter
import com.charlag.tuta.mail.ListedAttachment
import kotlinx.android.synthetic.main.activity_compose.*
import kotlinx.coroutines.launch

class ComposeActivity : AppCompatActivity() {
    private val viewModel: ComposeViewModel by viewModels()
    // Should be Attachment eventually
    private val attachmentAdapter =
        AttachmentAdapter<ListedFileReference>(
            iconRes = R.drawable.ic_close_black_24dp,
            onItemSelected = {
                // no-op for now
            },
            onAction = {
                viewModel.removeAttachment(it.file)
            }
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val senderAdapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                mutableListOf<String>()
            )
        fromSpinner.adapter = senderAdapter

        viewModel.enabledMailAddresses.observe(this) { mailAddresses ->
            senderAdapter.clear()
            senderAdapter.addAll(mailAddresses)
            senderAdapter.notifyDataSetChanged()
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

        attachmentsRecycler.layoutManager = LinearLayoutManager(this)
        attachmentsRecycler.setHasFixedSize(true)
        attachmentsRecycler.adapter = attachmentAdapter

        viewModel.attachments.observe(this) {
            attachmentAdapter.items.clear()
            attachmentAdapter.items.addAll(it.map(::ListedFileReference))
            attachmentAdapter.notifyDataSetChanged()
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
        val send = menu.add(0, 1, 100, "Send")
            .setIcon(R.drawable.ic_send_black_24dp)
            .setOnMenuItemClickListener {
                send()
                true
            }
            .setVisible(true)
            .apply {
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            }

        menu.add(0, 2, 0, "Attach")
            .setIcon(R.drawable.ic_clip_rotate)
            .setOnMenuItemClickListener {
                showFilePickerDialog()
                true
            }
            .setVisible(true)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)

        viewModel.anyRecipients.observe(this) {
            send.isEnabled = it
            val color = getColor(if (it) R.color.grey_30 else R.color.grey_10)
            send.iconTintList = ColorStateList.valueOf(color)
        }
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

    private fun showFilePickerDialog() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
            .setType("*/*")
            .addCategory(Intent.CATEGORY_DEFAULT)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("Compose", "pick result $data")
        val uri = data!!.data!!
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (!cursor.moveToFirst()) return@use

            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            val fileRef = FileReference(
                cursor.getString(nameIndex),
                cursor.getLong(sizeIndex), uri
            )
            viewModel.addAttachment(fileRef)
        }
    }
}

data class ListedFileReference(val file: FileReference) : ListedAttachment {
    override val name: String
        get() = file.name
    override val size: Long
        get() = file.size
}