package com.example.challange.utility.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionHandler(private val preferences: PreferenceHelper) {
    companion object {
        const val PERMISSIONS_REQUEST_CODE = 679
    }

    fun hasPermission(context: Context, permission: String) = ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    fun requestPermission(fragment: Fragment,
                          permission: String,
                          onPermissionAlreadyGranted: () -> Unit) {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            fragment.requestPermissions(arrayOf(permission),
                PERMISSIONS_REQUEST_CODE
            )
        } else {
            onPermissionAlreadyGranted()
        }
    }

    fun handlePermissionsResult(data: PermissionsData,
                                handlePermissionGranted: () -> Unit,
                                handlePermissionDeniedWithNeverAskAgain: (String) -> Unit,
                                shouldShowRequestPermissionRationale: (String) -> Boolean
    ) {
        if (data.requestCode == PERMISSIONS_REQUEST_CODE) {
            for (i in data.permissions.indices) {
                val permission = data.permissions[i]
                if (permission == data.permissionToCheck && data.grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    handlePermissionGranted()
                } else if (shouldShowRequestPermissionRationale(permission)) {
                    // permission has been denied. Here we could show a rationale message about why user needs the permission.
                    // reset permission denied, reset flag to handle never ask again system dialog from scratch
                    setPermission(permission, false)
                    // here we could handle any other action. So far UI/UX does not require it
                } else {
                    // permission has been denied with "never ask again". Some specific screens may have some specific behaviour to override
                    handlePermissionDeniedWithNeverAskAgain(permission)
                }
            }
        } else {
            throw IllegalArgumentException("Unknown permissions request code: ${data.requestCode}")
        }
    }

    /**
     * This function handles the "never ask again" result [Fragment.onRequestPermissionsResult()]
     * When user selects Never ask again the listener will kick off any action. That means we
     * will update the UI immediately and we do not want that.
     * This method will avoid that first time adding a flag in preferences, so from second time
     * we will allow the action.
     */
    fun executeNeverAskAgainActionIfNeeded(permission: String, action: () -> Unit) {
        if (preferences.getBooleanPreference(permission, false)) {
            action()
        } else {
            setPermission(permission, true)
        }
    }

    private fun setPermission(permission: String, value: Boolean) {
        preferences.setPreference(permission, value)
    }

    @Suppress("ArrayInDataClass")
    data class PermissionsData(
            val requestCode: Int,
            val permissionToCheck: String,
            val permissions: Array<String>,
            val grantResults: IntArray
    )
}