package com.charlag.tuta.mail

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.charlag.tuta.BuildConfig
import com.charlag.tuta.R
import com.charlag.tuta.compose.ComposeActivity
import com.charlag.tuta.compose.ForwardInitData
import com.charlag.tuta.compose.ReplyInitData
import com.charlag.tuta.entities.tutanota.File
import io.ktor.client.features.ClientRequestException
import kotlinx.android.synthetic.main.activity_mail_viewer.*
import kotlinx.coroutines.launch
import java.io.IOException

class MailViewerFragment : Fragment() {

    private lateinit var unreadItem: MenuItem
    private lateinit var readItem: MenuItem
    private val viewModel: MailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_mail_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        val webViewClient = BlockingWebViewClient(context!!).apply {
            onExternalContentDetected = {
                externalContentView.post {
                    externalContentView.visibility = View.VISIBLE
                }
            }
        }
        webView.webViewClient = webViewClient

        externalContentView.setOnClickListener {
            if (webViewClient.blockingResources) {
                webViewClient.blockingResources = false
                webView.reload()
                loadExternalContentText.text = "Always load from this sender"
            } else {
                // Second click
                externalContentView.visibility = View.GONE
            }
        }
        toolbar.navigationIcon = view.context.getDrawable(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
            goBack()
        }

        val openedMail = viewModel.openedMail.value
        if (openedMail == null) {
            parentFragmentManager.popBackStack()
            return
        }
        subjectView.text = openedMail.subject
        senderNameView.text = openedMail.sender.name
        senderAddressView.text = openedMail.sender.address
        tryAgainButton.setOnClickListener {
            loadMailBody()
        }
        loadMailBody()
        loadAttachments()


        val iconColor = view.context.getColor(R.color.grey_30)
        val tint = ColorStateList.valueOf(iconColor)
        toolbar.menu.add("Archive").setIcon(R.drawable.ic_archive_black_24dp)
            .setOnMenuItemClickListener {
                archiveMails(toolbar, viewModel, listOf(openedMail.id))
                goBack()
                true
            }
            .setIconTintList(tint)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        toolbar.menu.add("Trash").setIcon(R.drawable.ic_delete_black_24dp)
            .setOnMenuItemClickListener {
                trashMails(toolbar, viewModel, listOf(openedMail.id))
                goBack()
                true
            }
            .setIconTintList(tint)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        toolbar.menu.add("Move to").setIcon(R.drawable.ic_folder_black_24dp)
            .setOnMenuItemClickListener {
                lifecycleScope.launch {
                    moveMails(toolbar, viewModel, listOf(openedMail.id))
                    goBack()
                }
                true
            }
            .setIconTintList(tint)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        readItem = toolbar.menu.add("Mark as read").setIcon(R.drawable.ic_email_black_24dp)
            .setOnMenuItemClickListener {
                markAsRead(toolbar, viewModel, listOf(openedMail.id), false)
                updateUnreadStatus(true)
                true
            }
            .setIconTintList(tint)
            .apply { setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM) }
        unreadItem = toolbar.menu.add("Mark as unread").setIcon(R.drawable.ic_email_black_24dp)
            .setOnMenuItemClickListener {
                markAsRead(toolbar, viewModel, listOf(openedMail.id), true)
                updateUnreadStatus(false)
                true
            }
            .setIconTintList(tint)
            .apply { setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM) }

        updateUnreadStatus(openedMail.unread)

        replyButton.setOnClickListener {
            val openedMail = viewModel.openedMail.value!!
            val intent = ComposeActivity.intentForReply(
                context!!, ReplyInitData(
                    mailId = openedMail.id,
                    listId = openedMail.listId,
                    replyAll = false,
                    loadExternalContent = !webViewClient.blockingResources
                )
            )
            startActivity(intent)
        }

        moreButton.setOnClickListener {
            PopupMenu(moreButton.context, moreButton).apply {
                menu.add("Forward").setOnMenuItemClickListener {
                    val openedMail = viewModel.openedMail.value!!
                    val intent = ComposeActivity.intentForForward(
                        context!!, ForwardInitData(
                            mailId = openedMail.id,
                            listId = openedMail.listId,
                            loadExternalContent = !webViewClient.blockingResources
                        )
                    )
                    startActivity(intent)
                    true
                }
                show()
            }
        }
    }

    private fun updateUnreadStatus(unread: Boolean) {
        // This is not pretty (should be reactive) but will do for now
        unreadItem.isVisible = unread
        readItem.isVisible = !unread
    }

    private fun loadMailBody() {
        val openedMail = viewModel.openedMail.value!!
        lifecycleScope.launch {
            tryAgainButton.visibility = View.GONE
            try {
                val body = viewModel.loadMailBody(openedMail.body)
                webView.loadData(body.text, "text/html", "UTF-8")
            } catch (e: IOException) {
                tryAgainButton.visibility = View.VISIBLE
            } catch (e: ClientRequestException) {
                // Log for now. Can happen if we are not logged in
                Log.w("Mails", "Failed to load mail body %e")
                tryAgainButton.visibility = View.VISIBLE
            }
        }
    }

    private fun loadAttachments() {
        lifecycleScope.launch {
            val files = viewModel.loadAttachments(viewModel.openedMail.value!!)
                .map(::ListedAttachmentFile)
            val adapter = AttachmentAdapter<ListedAttachmentFile>(
                iconRes = R.drawable.ic_file_download_black_24dp,
                onItemSelected = { file ->
                    viewModel.openFile(file.file)
                },
                onAction = { file ->
                    viewModel.downloadFile(file.file)
                })
            adapter.items.addAll(files)
            attachmentsRecycler.adapter = adapter
            attachmentsRecycler.layoutManager = LinearLayoutManager(attachmentsRecycler.context)
            attachmentsRecycler.setHasFixedSize(true)
        }
    }

    private fun goBack() {
        parentFragmentManager.popBackStack()
    }
}

data class ListedAttachmentFile(val file: File) : ListedAttachment {
    override val name: String
        get() = file.name
    override val size: Long
        get() = file.size
}