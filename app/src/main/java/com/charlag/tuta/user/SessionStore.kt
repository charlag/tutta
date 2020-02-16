package com.charlag.tuta.user

import android.content.Context
import android.preference.PreferenceManager
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.Id
import com.charlag.tuta.notifications.base64ToBytes
import com.charlag.tuta.notifications.bytesToBase64
import com.charlag.tuta.toBase64
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface SessionStore {
    var lastUser: Id?
    fun getDeviceKey(): ByteArray?
    fun storeDeviceKey(encKey: ByteArray)
    fun saveSessionData(credentials: Credentials)
    fun loadSessionData(userId: Id): Credentials?
    fun loadAllCredentials(): List<Credentials>
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

    override fun getDeviceKey(): ByteArray? {
        return prefs.getString(DEVICE_KEY_KEY, null)?.let(::base64ToBytes)
    }

    override fun storeDeviceKey(encKey: ByteArray) {
        check(getDeviceKey() == null) { "Trying to overwrite existing device key" }
        prefs.edit().putString(DEVICE_KEY_KEY, encKey.toBase64()).apply()
        assert(encKey.contentEquals(getDeviceKey()!!))
    }

    override var lastUser: Id?
        get() = prefs.getString(LAST_USER_KEY, null)?.let(::GeneratedId)
        set(value) = prefs.edit().putString(LAST_USER_KEY, value?.asString()).apply()

    override fun saveSessionData(credentials: Credentials) {
        val jsonString = json.stringify(Credentials.serializer(), credentials)
        credentialsPref.edit()
            .putString(credentials.userId.asString(), jsonString)
            .apply()
    }

    override fun loadAllCredentials(): List<Credentials> {
        return credentialsPref.all.values.map {
            parseCrdentials(it as String)
        }
    }

    override fun loadSessionData(userId: Id): Credentials? {
        return credentialsPref.getString(userId.asString(), null)
            ?.let { parseCrdentials(it) }
    }

    private fun parseCrdentials(it: String) = json.parse(Credentials.serializer(), it)

    override fun removeSessionData(userId: Id) {
        credentialsPref.edit().remove(userId.asString()).apply()
    }

    companion object {
        const val LAST_USER_KEY = "LAST_USER"
        const val DEVICE_KEY_KEY = "DEVICE_KEY"
        const val USER_CREDENTIALS_PREF = "CREDENTIALS"
    }
}