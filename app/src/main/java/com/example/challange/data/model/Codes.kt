package com.example.challange.data.model

import com.squareup.moshi.Json

data class Codes(
    @Json(name = "admin_district") val admin_district: String,
    @Json(name ="admin_county") val admin_county: String,
    @Json(name ="admin_ward") val admin_ward: String,
    @Json(name ="parish") val parish: String,
    @Json(name ="parliamentary_constituency") val parliamentary_constituency: String,
    @Json(name ="ccg") val ccg: String,
    @Json(name ="ccg_id") val ccg_id: String,
    @Json(name ="ced") val ced: String,
    @Json(name ="nuts") val nuts: String
)