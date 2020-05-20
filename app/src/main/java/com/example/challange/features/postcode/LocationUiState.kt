package com.example.challange.features.postcode

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.example.challange.features.uimodels.LocationUiModel

data class LocationUiState(
    val uiState: Async<LocationUiModel> = Uninitialized
) : MvRxState