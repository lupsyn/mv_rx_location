package com.example.challange.data.model

import com.squareup.moshi.Json

data class Response(
    @Json(name = "status") val status: Int,
    @Json(name = "result") val result: List<Location>? = null
)