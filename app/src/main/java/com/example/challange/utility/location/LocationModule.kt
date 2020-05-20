package com.example.challange.utility.location

import com.example.challange.ChallengeApplication
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import io.reactivex.schedulers.Schedulers

object LocationModule {
    private fun fusedApiClient() = LocationServices.getFusedLocationProviderClient(
        ChallengeApplication.getApp()
    )

    private fun locationSettings(): SettingsClient = LocationServices.getSettingsClient(
        ChallengeApplication.getApp()
    )

    fun locationClient(): LocationClientImpl =
        LocationClientImpl(fusedApiClient(), locationSettings(), Schedulers.computation())
}