package com.example.challange.features.uimodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Coordinates(val latitude: Double, val longitude: Double) : Parcelable {
    override fun toString(): String {
        return latitude.toString() + longitude.toString()
    }
}

