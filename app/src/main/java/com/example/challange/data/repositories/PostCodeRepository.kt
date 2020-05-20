package com.example.challange.data.repositories

import com.example.challange.features.uimodels.LocationUiModel
import io.reactivex.Single

interface PostCodeRepository {
    fun fetchPostCode(): Single<LocationUiModel>
}
