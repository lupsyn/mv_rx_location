package com.example.challange.utility.location

import android.content.IntentSender
import android.os.HandlerThread
import com.example.challange.ChallengeApplication.Companion.TIMEOUT
import com.example.challange.features.uimodels.Coordinates
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import io.reactivex.Scheduler
import io.reactivex.Single
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

typealias OnLocationSucceed = (Coordinates) -> Unit
typealias OnLocationFailed = (Exception) -> Unit



/**
 * client.lastLocation may return null Location inside addOnSuccessListener.
 * If this happens we are forced to request the location manually (client.requestLocationUpdates).
 * But before that we need to ensure the location settings are ok (settingsClient.checkLocationSettings).
 * If we succeed here then we allow the manual request.
 * If we fail we propagate the error. In this case the exception will have an error code [LocationSettingsStatusCodes.RESOLUTION_REQUIRED]
 * This error code means the user needs to change the location settings.
 *
 * Some situations that can make the location fail in the settings check:
 * Some devices can have doggy hardware and for example we can find devices where choosing (in Location  Settings)
 * "GPS only" or "Battery safe" will end up with fail whereas in other devices will work.
 *
 */

@SuppressWarnings("MissingPermission")
class LocationClientImpl(
    private val client: FusedLocationProviderClient,
    private val settingsClient: SettingsClient,
    private val scheduler: Scheduler
) : LocationClient {

    private val lopperThread: HandlerThread by lazy {
        HandlerThread("locationThread")
    }

    override fun getLastLocation(): Single<Coordinates> =
        Single.create<Coordinates> { e ->
            client.lastLocation
                .addOnSuccessListener(Executors.newSingleThreadExecutor(), OnSuccessListener {
                    if (it == null) {
                        checkLocationSettings(
                            { latLng -> e.onSuccess(latLng) },
                            { ex -> e.tryOnError(ex) }
                        )
                    } else {
                        e.onSuccess(Coordinates(it.latitude, it.longitude))
                    }
                })
                .addOnFailureListener { e.tryOnError(it) }
        }.timeout(TIMEOUT, TimeUnit.SECONDS, scheduler)
            .doFinally { lopperThread.quit() }

    private fun checkLocationSettings(
        onLocationSucceed: OnLocationSucceed,
        onLocationFailed: OnLocationFailed
    ) {
        val locationRequest = locationRequest()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val result = settingsClient.checkLocationSettings(builder.build())
        result.addOnCompleteListener(Executors.newSingleThreadExecutor(), OnCompleteListener {
            try {
                it.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
                lopperThread.start()
                client.requestLocationUpdates(
                    locationRequest,
                    locationCallback(onLocationSucceed),
                    lopperThread.looper
                )
            } catch (exception: ApiException) {
                if (exception.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    try {
                        // Cast to a resolvable exception.
                        onLocationFailed(exception as ResolvableApiException)
                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    } catch (e: ClassCastException) {
                        // Ignore, should be an impossible error.
                    }
                }
            }
        })
    }

    private fun locationCallback(onLocationSucceed: OnLocationSucceed): LocationCallback =
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                if (locationResult != null && locationResult.locations.isNotEmpty()) {
                    onLocationSucceed(
                        Coordinates(
                            locationResult.locations.first().latitude,
                            locationResult.locations.first().longitude
                        )
                    )
                }
                client.removeLocationUpdates(this)
            }
        }

    /**
     * As described in Location best practices at https://developer.android.com/guide/topics/location/battery#timeouts
     * it is a good idea to set set a reasonable timeout when location updates should stop
     * So here we set [LocationRequest.setExpirationDuration] [LOCATION_REQUEST_EXPIRATION_DURATION] which means that location request will expire after 15 seconds
     * if location is not received and location request not removed @see FusedLocationProviderClient.removeLocationUpdates
     */
    private fun locationRequest(): LocationRequest = LocationRequest().apply {
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        fastestInterval = FASTEST_INTERVAL
        interval = INTERVAL
        numUpdates = UPDATES
        setExpirationDuration(LOCATION_REQUEST_EXPIRATION_DURATION)
    }

    companion object {
        private const val LOCATION_REQUEST_EXPIRATION_DURATION = 15000L
        private const val FASTEST_INTERVAL = 5000L
        private const val INTERVAL = 10000L
        private const val UPDATES = 1
    }

}