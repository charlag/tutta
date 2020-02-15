package com.charlag.tuta.user

import android.content.Context
import android.preference.PreferenceManager
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.Id
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface SessionStore {
    var lastUser: Id?

    fun saveSessionData(credentials: Credentials)
    fun loadSessionData(userId: Id): Credentials?
    fun removeSessionData(userId: Id)
}

@Serializable
data class Credentials(
    val userId: Id,
    val accessToken: String,
    val encPassword: ByteArray,
    val mailAddress: String
)

class SharedPrefSessionStore(
    context: Context,
    private val json: Json
) : SessionStore {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val credentialsPref =
        context.getSharedPreferences(USER_CREDENTIALS_PREF, Context.MODE_PRIVATE)

    override var lastUser: Id?
        get() = prefs.getString(LAST_USER_KEY, null)?.let(::GeneratedId)
        set(value) = prefs.edit().putString(LAST_USER_KEY, value?.asString()).apply()

    override fun saveSessionData(credentials: Credentials) {
        val jsonString = json.stringify(Credentials.serializer(), credentials)
        credentialsPref.edit()
            .putString(credentials.userId.asString(), jsonString)
            .apply()
    }

    override fun loadSessionData(userId: Id): Credentials? {
        return credentialsPref.getString(userId.asString(), null)
            ?.let { json.parse(Credentials.serializer(), it) }
    }

    override fun removeSessionData(userId: Id) {
        credentialsPref.edit().remove(userId.asString()).apply()
    }

    companion object {
        const val LAST_USER_KEY = "LAST_USER"
        const val USER_CREDENTIALS_PREF = "CREDENTIALS"
    }
}