package com.charlag.tuta

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_mail_viewer.*
import kotlinx.coroutines.launch

class MailViewerFragment : Fragment() {

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
        lifecycleScope.launch {
            val body = viewModel.loadMailBody(openedMail.body)
            webView.loadData(body.compressedText ?: body.text, "text/html", "UTF-8")
        }

        val iconColor = view.context.getColor(R.color.primaryOnSurface)
        toolbar.menu.add("Archive").setIcon(R.drawable.ic_archive_black_24dp)
            .setIconTintList(ColorStateList.valueOf(iconColor))
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        toolbar.menu.add("Trash").setIcon(R.drawable.ic_delete_black_24dp)
            .setIconTintList(ColorStateList.valueOf(iconColor))
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        toolbar.menu.add("Move to")
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)

    }
}