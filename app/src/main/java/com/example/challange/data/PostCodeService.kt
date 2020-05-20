package com.example.challange.data

import com.example.challange.data.model.Response
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface PostCodesService {

    @GET("/postcodes?")
    fun fetchPostCode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Single<Response>
}