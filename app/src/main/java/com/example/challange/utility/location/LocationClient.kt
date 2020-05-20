package com.example.challange.utility.location

import com.example.challange.features.uimodels.Coordinates
import io.reactivex.Single

interface LocationClient {
    fun getLastLocation(): Single<Coordinates>
}