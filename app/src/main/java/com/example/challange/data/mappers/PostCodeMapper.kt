package com.example.challange.data.mappers

import com.example.challange.data.model.Response
import com.example.challange.features.uimodels.Coordinates
import com.example.challange.features.uimodels.LocationUiModel
import com.example.challange.features.uimodels.PostCode

class PostCodeMapper {
    fun map(coordinates: Coordinates, networkResponse: Response): LocationUiModel {
        val firstLocation = networkResponse.result?.firstOrNull()
        return if (firstLocation != null) {
            LocationUiModel(PostCode(firstLocation.postcode), coordinates)
        } else {
            LocationUiModel(PostCode(), coordinates)
        }
    }
}