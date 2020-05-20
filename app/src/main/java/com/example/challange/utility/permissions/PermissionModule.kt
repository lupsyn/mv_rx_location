package com.example.challange.utility.permissions

import android.content.Context
import android.content.SharedPreferences
import com.example.challange.ChallengeApplication
import com.example.challange.ChallengeApplication.Companion.SHARED_PREFERENCES_NAME

object PermissionModule {

    fun permissionHandler() =
        PermissionHandler(
            PreferenceHelper(
                sharedPreferences()
            )
        )

    private fun sharedPreferences(): SharedPreferences {
        return ChallengeApplication.getApp().getSharedPreferences(
            SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }
}