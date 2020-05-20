package com.example.challange.features.uimodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostCode(val postCodeValue: String? = INVALID_POSTCODE) : Parcelable

const val INVALID_POSTCODE = "not found"

