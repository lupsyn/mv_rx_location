package com.example.challange.features.postcode

import com.airbnb.mvrx.*
import com.example.challange.data.PostCodesService
import com.example.challange.data.mappers.PostCodeMapper
import com.example.challange.data.repositories.PostCodeRepository
import com.example.challange.data.repositories.PostCodeRepositoryImpl
import com.example.challange.utility.location.LocationModule
import com.example.challange.utility.location.PermissionState
import com.example.challange.utility.location.PermissionState.*
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject

class PostCodeViewModel(
    @PersistState private val initialState: LocationUiState,
    private val repository: PostCodeRepository
) : BaseMvRxViewModel<LocationUiState>(initialState, debugMode = true) {

    init {
        setState { copy(uiState = Uninitialized) }
    }

    fun onPermissionChange(permissionState: PermissionState) {
        when (permissionState) {
            Granted -> requestLocation()
            Denied -> setState { copy(uiState = Fail(error = Exception("We need location permission to make this app works !"))) }
            DeniedForever -> setState { copy(uiState = Fail(error = Exception("This app will not work if you don't enable location permissions!"))) }
        }
    }

    fun requestLocation() {
        repository.fetchPostCode()
            .doOnSubscribe { setState { copy(uiState = Loading()) } }
            .subscribeOn(Schedulers.computation())
            .execute { copy(uiState = it) }
    }

    companion object : MvRxViewModelFactory<PostCodeViewModel, LocationUiState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: LocationUiState
        ): PostCodeViewModel? {
            val service: PostCodesService by viewModelContext.activity.inject()
            val repository: PostCodeRepository = PostCodeRepositoryImpl(
                service,
                LocationModule.locationClient(),
                PostCodeMapper()
            )
            return PostCodeViewModel(state, repository)
        }
    }
}