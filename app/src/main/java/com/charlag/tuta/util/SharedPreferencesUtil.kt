package com.charlag.tuta.util

import android.content.SharedPreferences

operator fun SharedPreferences.get(key: String): String? = getString(key, null)
operator fun SharedPreferences.set(key: String, intValue: Int) {
    edit().putInt(key, intValue).apply()
}

operator fun SharedPreferences.set(key: String, boolValue: Boolean) {
    edit().putBoolean(key, boolValue).apply()
}

operator fun SharedPreferences.set(key: String, longValue: Long) {
    edit().putLong(key, longValue).apply()
}

operator fun SharedPreferences.set(key: String, stringValue: String) {
    edit().putString(key, stringValue).apply()
}