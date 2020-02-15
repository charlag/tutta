package com.charlag.tuta.compose

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Parcelable
import android.provider.OpenableColumns
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.charlag.tuta.AuthenticatedActivity
import com.charlag.tuta.R
import com.charlag.tuta.di.ViewModelFactory
import com.charlag.tuta.mail.AttachmentAdapter
import com.charlag.tuta.mail.BlockingWebViewClient
import com.charlag.tuta.mail.ListedAttachment
import com.charlag.tuta.util.setIconTintListCompat
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_compose.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class ComposeActivity : AuthenticatedActivity(R.layout.activity_compose) {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: ComposeViewModel by viewModels { viewModelFactory }
    private val webClient by lazy { BlockingWebViewClient(this) }

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

        replyWebView.webViewClient = webClient

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


        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            when {
                intent.hasExtra(LOCAL_DRAFT_EXTRA) -> {
                    val localDraftId = intent.getLongExtra(LOCAL_DRAFT_EXTRA, 0)
                    viewModel.initWIthLocalDraftId(localDraftId)?.let(::init)
                }
                intent.hasExtra(REPLY_DATA) -> {
                    val replyInitData = intent.getParcelableExtra<ReplyInitData>(REPLY_DATA)
                    init(viewModel.initWithReplyInitData(replyInitData))
                }
                intent.hasExtra(FORWARD_DATA) -> {
                    val forwardInitData = intent.getParcelableExtra<ForwardInitData>(FORWARD_DATA)
                    init(viewModel.initWithForwardData(forwardInitData))
                }
                intent.hasExtra(DRAFT_DATA) -> {
                    val draftInitData = intent.getParcelableExtra<DraftInitData>(DRAFT_DATA)
                    init(viewModel.initWithDraftData(draftInitData))
                }
            }
        }
    }

    override fun onBackPressed() {
        onBeforeFinish()
    }

    private fun onBeforeFinish() {
        lifecycleScope.launch {
            if (fromSpinner != null && viewModel.saveDraft(
                    subjectField.text.toString(),
                    contentField.text,
                    fromSpinner.selectedItem as String?
                )
            ) {
                Toast.makeText(this@ComposeActivity, "Saving draft", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    private fun init(initData: InitData) {
        subjectField.setText(initData.subject)
        contentField.setText(initData.content)
        initData.replyContent?.let {
            replyWebView.isVisible = true
            replyWebViewSeparator.isVisible = true
            replyWebView.loadData(it, "text/html", Charsets.UTF_8.name())
            webClient.blockingResources = !initData.loadExternalContent
        }
        // Adding spaces after mail address so that field watchers
        // understand that it's another address
        for (toRecipient in initData.toRecipients) {
            toField.text.append(toRecipient.mailAddress + " ")
        }
        for (ccRecipient in initData.ccRecipients) {
            ccField.text.append(ccRecipient.mailAddress + " ")
        }
        for (bccRecipient in initData.bccRecipients) {
            bccField.text.append(bccRecipient.mailAddress + " ")
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
            onBeforeFinish()
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
            send.setIconTintListCompat(ColorStateList.valueOf(color))
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun send() {
        Toast.makeText(this, "Sending….", Toast.LENGTH_SHORT).show()
        val activity = this

        lifecycleScope.launch {
            try {
                viewModel.send(
                    subjectField.text.toString(),
                    contentField.text,
                    fromSpinner.selectedItem as String
                )
                onBeforeFinish()
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
        val uri = data?.data ?: return
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (!cursor.moveToFirst()) return@use

            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            val fileRef = FileReference(
                cursor.getString(nameIndex),
                cursor.getLong(sizeIndex), uri.toString()
            )
            viewModel.addAttachment(fileRef)
        }
    }

    companion object {
        const val LOCAL_DRAFT_EXTRA = "localDraft"
        private const val REPLY_DATA = "replyData"
        private const val FORWARD_DATA = "forwardData"
        private const val DRAFT_DATA = "draftData"

        private inline fun newIntent(context: Context, builder: Intent.() -> Unit) =
            Intent(context, ComposeActivity::class.java).apply(builder)

        fun intentForReply(context: Context, replyInitData: ReplyInitData): Intent {
            return newIntent(context) {
                putExtra(REPLY_DATA, replyInitData)
            }
        }

        fun intentForForward(context: Context, forwardInitData: ForwardInitData): Intent {
            return newIntent(context) {
                putExtra(FORWARD_DATA, forwardInitData)
            }
        }

        fun intentEditDraft(context: Context, draftInitData: DraftInitData): Intent {
            return newIntent(context) {
                putExtra(DRAFT_DATA, draftInitData)
            }
        }
    }
}

@Parcelize
data class ReplyInitData(
    val mailId: String,
    val listId: String,
    val replyAll: Boolean,
    val loadExternalContent: Boolean
) : Parcelable


@Parcelize
data class ForwardInitData(
    val mailId: String,
    val listId: String,
    val loadExternalContent: Boolean
) : Parcelable

@Parcelize
data class DraftInitData(
    val draftId: String,
    val listId: String
) : Parcelable

data class ListedFileReference(val file: DraftFile) : ListedAttachment {
    override val name: String
        get() = file.name
    override val size: Long
        get() = file.size
}