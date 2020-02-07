package com.charlag.tuta.mail

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class BlockingWebViewClient(
    private val context: Context
) : WebViewClient() {
    var blockingResources = true
    var detectedExternalContent = false
    var onExternalContentDetected: (() -> Unit)? = null

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        return if (blockingResources && request.url.scheme != "data") {
            if (!detectedExternalContent) {
                detectedExternalContent = true
                onExternalContentDetected?.invoke()
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
            context.startActivity(Intent(Intent.ACTION_VIEW, request.url))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT)
                .show()
        }
        return true
    }
}