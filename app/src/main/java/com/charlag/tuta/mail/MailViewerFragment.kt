package com.charlag.tuta.mail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.charlag.tuta.BuildConfig
import com.charlag.tuta.R
import io.ktor.client.features.ClientRequestException
import kotlinx.android.synthetic.main.activity_mail_viewer.*
import kotlinx.coroutines.launch
import java.io.IOException

class MailViewerFragment : Fragment() {

    private lateinit var unreadItem: MenuItem
    private lateinit var readItem: MenuItem
    val viewModel: MailViewModel by activityViewModels()

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
        var blockingResources = true
        var detectedExternalContent = false
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return if (blockingResources && request.url.scheme != "data") {
                    if (!detectedExternalContent) {
                        detectedExternalContent = true
                        externalContentView.post {
                            externalContentView.visibility = View.VISIBLE
                        }
                    }
                    WebResourceResponse("text/html", "UTF-8", 403, "Blocked", null, null)
                } else {
                    null
                }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, request.url))
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT)
                        .show()
                }
                return true
            }
        }
        externalContentView.setOnClickListener {
            if (blockingResources) {
                blockingResources = false
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
        senderNameView.visibility =
            if (openedMail.sender.name.isBlank()) View.GONE else View.VISIBLE
        senderAddressView.text = openedMail.sender.address
        tryAgainButton.setOnClickListener {
            loadMailBody()
        }
        loadMailBody()
        loadAttachments()


        val iconColor = view.context.getColor(R.color.primaryOnSurface)
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
            val adapter = AttachmentAdapter(
                onItemSelected = { file ->
                    viewModel.openFile(file)
                },
                onDownloadFile = { file ->
                    viewModel.downloadFile(file)
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