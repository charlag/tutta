package com.charlag.tuta.mail

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.drawable.RotateDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.charlag.tuta.BuildConfig
import com.charlag.tuta.PreferenceFacade
import com.charlag.tuta.R
import com.charlag.tuta.compose.ComposeActivity
import com.charlag.tuta.compose.ForwardInitData
import com.charlag.tuta.compose.ReplyInitData
import com.charlag.tuta.data.MailAddressEntity
import com.charlag.tuta.data.MailEntity
import com.charlag.tuta.entities.IdTuple
import com.charlag.tuta.entities.tutanota.File
import com.charlag.tuta.util.IdTupleWrapper
import com.charlag.tuta.util.setIconTintListCompat
import com.charlag.tuta.util.toWrapper
import com.charlag.tuta.util.unwrap
import dagger.android.support.DaggerFragment
import io.ktor.client.features.ClientRequestException
import kotlinx.android.synthetic.main.activity_mail_viewer.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.DateFormat
import javax.inject.Inject

class MailViewerFragment : DaggerFragment() {

    private lateinit var unreadItem: MenuItem
    private lateinit var readItem: MenuItem
    private val viewModel: MailViewModel by activityViewModels()
    @Inject
    lateinit var preferenceFacade: PreferenceFacade

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_mail_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mailId = arguments?.getParcelable<IdTupleWrapper>(MAIL_ID_ARG)
        lifecycleScope.launch {
            val openedMail = mailId?.let { viewModel.getMail(it.unwrap()) }
            if (openedMail == null) {
                parentFragmentManager.popBackStack()
                return@launch
            }
            if (openedMail.unread) {
                launch {
                    viewModel.markAsRead(listOf(openedMail.id))
                }
            }

            if (BuildConfig.DEBUG) {
                WebView.setWebContentsDebuggingEnabled(true)
            }
            webView.settings.setSupportZoom(true)
            webView.settings.builtInZoomControls = true
            webView.settings.displayZoomControls = false
            val webViewClient = BlockingWebViewClient(context!!).apply {
                onExternalContentDetected = {
                    externalContentView.post {
                        // TODO: may crash here if fragment closed too early
                        externalContentView.visibility = View.VISIBLE
                    }
                }
                blockingResources =
                    !preferenceFacade.allowedToLoadExternalContent(openedMail.sender.address)
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
                    preferenceFacade.setAllowedToLoadExternalContent(
                        openedMail.sender.address,
                        true
                    )
                }
            }
            toolbar.navigationIcon = view.context.getDrawable(R.drawable.ic_arrow_back_black_24dp)
            toolbar.setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
                goBack()
            }

            subjectView.text = openedMail.subject
            senderNameView.text = openedMail.sender.name
            senderAddressView.text = openedMail.sender.address
            tryAgainButton.setOnClickListener {
                loadMailBody(openedMail)
            }
            loadMailBody(openedMail)
            loadAttachments(openedMail)


            val iconColor = view.context.getColor(R.color.grey_30)
            val tint = ColorStateList.valueOf(iconColor)
            toolbar.menu.add("Archive").setIcon(R.drawable.ic_archive_black_24dp)
                .setOnMenuItemClickListener {
                    archiveMails(toolbar, viewModel, listOf(openedMail.id))
                    goBack()
                    true
                }
                .setIconTintListCompat(tint)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            toolbar.menu.add("Trash").setIcon(R.drawable.ic_delete_black_24dp)
                .setOnMenuItemClickListener {
                    trashMails(toolbar, viewModel, listOf(openedMail.id))
                    goBack()
                    true
                }
                .setIconTintListCompat(tint)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            toolbar.menu.add("Move to").setIcon(R.drawable.ic_folder_black_24dp)
                .setOnMenuItemClickListener {
                    lifecycleScope.launch {
                        moveMails(toolbar, viewModel, listOf(openedMail.id))
                        goBack()
                    }
                    true
                }
                .setIconTintListCompat(tint)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            readItem = toolbar.menu.add("Mark as read").setIcon(R.drawable.ic_email_black_24dp)
                .setOnMenuItemClickListener {
                    markAsRead(toolbar, viewModel, listOf(openedMail.id), false)
                    updateUnreadStatus(nowUnread = false)
                    true
                }
                .setIconTintListCompat(tint)
                .apply { setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM) }
            unreadItem = toolbar.menu.add("Mark as unread").setIcon(R.drawable.ic_email_black_24dp)
                .setOnMenuItemClickListener {
                    markAsRead(toolbar, viewModel, listOf(openedMail.id), true)
                    updateUnreadStatus(nowUnread = true)
                    true
                }
                .setIconTintListCompat(tint)
                .apply { setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM) }

            updateUnreadStatus(nowUnread = openedMail.unread)

            replyButton.setOnClickListener {
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

            var showingMailDetails = false
            emailDetailsButton.setOnClickListener {
                showingMailDetails = !showingMailDetails
                mailDetailsView.isGone = !showingMailDetails
                val buttonDrawable = (emailDetailsButton.drawable as RotateDrawable)
                if (showingMailDetails) {
                    ObjectAnimator.ofInt(buttonDrawable, "level", 0, 5000).start()
                } else {
                    ObjectAnimator.ofInt(buttonDrawable, "level", 5000, 0).start()
                }
            }

            setupDetailsField(toLabel, toAddressLabel, openedMail.toRecipients)
            setupDetailsField(ccLabel, ccAddressLabel, openedMail.ccRecipients)
            setupDetailsField(bccLabel, bccAddressLabel, openedMail.bccRecipients)
            setupDetailsField(replyToLabel, replyToAddressLabel, openedMail.replyTos)
            sentValueLabel.text = DateFormat.getDateInstance().format(openedMail.sentDate)

            if (openedMail.differentEnvelopeSender != null) {
                warningIcon.isVisible = true
                realSenderLabel.isVisible = true
                realSenderAddressLabel.isVisible = true
                realSenderAddressLabel.text = openedMail.differentEnvelopeSender
            }
        }
    }

    private fun setupDetailsField(
        label: TextView,
        addressLabel: TextView,
        recipients: List<MailAddressEntity>
    ) {
        addressLabel.text = recipients.joinToString(", ") {
            if (it.name.isEmpty()) it.address
            else "${it.name} • ${it.address}"
        }
        addressLabel.isGone = recipients.isEmpty()
        label.isGone = recipients.isEmpty()
    }

    private fun updateUnreadStatus(nowUnread: Boolean) {
        // This is not pretty (should be reactive) but will do for now
        unreadItem.isVisible = !nowUnread
        readItem.isVisible = nowUnread
    }

    private fun loadMailBody(mail: MailEntity) {
        lifecycleScope.launch {
            tryAgainButton.visibility = View.GONE
            try {
                val body = viewModel.loadMailBody(mail.body)
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

    private fun loadAttachments(mail: MailEntity) {
        lifecycleScope.launch {
            val files = viewModel.loadAttachments(mail)
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

    companion object {
        private const val MAIL_ID_ARG = "mailId"
        fun withMailId(id: IdTuple) = MailViewerFragment().apply {
            arguments = Bundle().apply {
                putParcelable(MAIL_ID_ARG, id.toWrapper())
            }
        }
    }
}

internal data class ListedAttachmentFile(val file: File) : ListedAttachment {
    override val name: String
        get() = file.name
    override val size: Long
        get() = file.size
}