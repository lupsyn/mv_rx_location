package com.example.challange.utility.permissions

import android.content.SharedPreferences

/**
 * Wrapper around Android's shared preferences system.
 * Ideally all preference-related operations should go through here.
 */
class PreferenceHelper(private val sharedPreferences: SharedPreferences) {

    fun getBooleanPreference(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun setPreference(key: String, value: Boolean) {
        sharedPreferences
            .edit()
            .putBoolean(key, value)
            .apply()
    }
}