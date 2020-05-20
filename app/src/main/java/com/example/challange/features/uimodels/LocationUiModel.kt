package com.example.challange.features.uimodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationUiModel(val postCode: PostCode, val coordinates: Coordinates) : Parcelable