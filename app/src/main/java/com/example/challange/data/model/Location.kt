package com.example.challange.data.model

import com.squareup.moshi.Json

data class Location(
    @Json(name ="postcode") val postcode: String,
    @Json(name ="quality") val quality: Int,
    @Json(name ="eastings") val eastings: Int,
    @Json(name ="northings") val northings: Int,
    @Json(name ="country") val country: String,
    @Json(name ="nhs_ha") val nhs_ha: String,
    @Json(name ="longitude") val longitude: Double,
    @Json(name ="latitude") val latitude: Double,
    @Json(name ="european_electoral_region") val european_electoral_region: String,
    @Json(name ="primary_care_trust") val primary_care_trust: String,
    @Json(name ="region") val region: String,
    @Json(name ="lsoa") val lsoa: String,
    @Json(name ="msoa") val msoa: String,
    @Json(name ="incode") val incode: String,
    @Json(name ="outcode") val outcode: String,
    @Json(name ="parliamentary_constituency") val parliamentary_constituency: String,
    @Json(name ="admin_district") val admin_district: String,
    @Json(name ="parish") val parish: String,
    @Json(name ="admin_county") val admin_county: String,
    @Json(name ="admin_ward") val admin_ward: String,
    @Json(name ="ced") val ced: String,
    @Json(name ="ccg") val ccg: String,
    @Json(name ="nuts") val nuts: String,
    @Json(name ="codes") val codes: Codes,
    @Json(name ="distance") val distance: Double
)