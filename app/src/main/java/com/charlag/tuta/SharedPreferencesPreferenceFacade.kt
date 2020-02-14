package com.charlag.tuta

import android.content.Context

interface PreferenceFacade {
    fun allowedToLoadExternalContent(senderAddress: String): Boolean
    fun setAllowedToLoadExternalContent(senderAddress: String, allowed: Boolean)
}

class SharedPreferencesPreferenceFacade(context: Context) : PreferenceFacade {
    private val prefs =
        context.getSharedPreferences(EXTERNAL_CONTENT_EXCEPTIONS, Context.MODE_PRIVATE)

    override fun allowedToLoadExternalContent(senderAddress: String) = prefs.contains(senderAddress)

    override fun setAllowedToLoadExternalContent(senderAddress: String, allowed: Boolean) {
        prefs.edit().putBoolean(senderAddress, allowed).apply()
    }

    companion object {
        private const val EXTERNAL_CONTENT_EXCEPTIONS = "external_content_exceptions"
    }
}