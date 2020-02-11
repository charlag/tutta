package com.charlag.tuta

import android.content.Context

class PreferenceFacade(context: Context) {
    private val prefs =
        context.getSharedPreferences(EXTERNAL_CONTENT_EXCEPTIONS, Context.MODE_PRIVATE)

    fun allowedToLoadExternalContent(senderAddress: String) = prefs.contains(senderAddress)

    fun setAllowedToLoadExternalContent(senderAddress: String, allowed: Boolean) {
        prefs.edit().putBoolean(senderAddress, allowed).apply()
    }

    companion object {
        private const val EXTERNAL_CONTENT_EXCEPTIONS = "external_content_exceptions"
    }
}