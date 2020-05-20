package com.example.challange.utility.location
sealed class PermissionState {
    object Granted : PermissionState()
    object Denied : PermissionState()
    object DeniedForever : PermissionState()
}