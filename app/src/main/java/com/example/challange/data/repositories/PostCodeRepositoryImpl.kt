package com.example.challange.data.repositories

import com.example.challange.data.PostCodesService
import com.example.challange.data.mappers.PostCodeMapper
import com.example.challange.features.uimodels.LocationUiModel
import com.example.challange.utility.espresso.EspressoIdlingResource
import com.example.challange.utility.location.LocationClient
import io.reactivex.Single

class PostCodeRepositoryImpl(
    private val api: PostCodesService,
    private val locationClient: LocationClient,
    private val mapper: PostCodeMapper
) : PostCodeRepository {

    override fun fetchPostCode(): Single<LocationUiModel> =
        locationClient.getLastLocation()
            .doOnSubscribe { EspressoIdlingResource.increment() }
            .doAfterTerminate { EspressoIdlingResource.decrement() }
            .flatMap { coordinates ->
                api.fetchPostCode(lat = coordinates.latitude, lon = coordinates.longitude)
                    .map { mapper.map(coordinates, it) }
            }
}