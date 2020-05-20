package com.example.challange.features.postcode

import com.airbnb.mvrx.*
import com.airbnb.mvrx.test.DebugMode
import com.airbnb.mvrx.test.MvRxTestRule
import com.example.challange.base.TestBase
import com.example.challange.data.repositories.PostCodeRepository
import com.example.challange.features.uimodels.LocationUiModel
import com.example.challange.utility.location.PermissionState
import io.reactivex.Single
import junit.framework.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.Mock
import org.mockito.Mockito

class PostCodeViewModelTest : TestBase() {

    private lateinit var underTest: PostCodeViewModel

    @Mock
    private lateinit var postCodeRepository: PostCodeRepository

    @Mock
    private lateinit var locationUiModel: LocationUiModel

    //  private lateinit var subListRepo: List<GitRepo>

    @get:Rule
    val mvrxRule = MvRxTestRule()


    @Before
    fun setUp() {

    }

    @Test
    fun `should retrieve location correctly once permission are granted`() {
        val initialState = LocationUiState()

        whenFetchRepositoryThenReturn(Single.just(locationUiModel))

        underTest = PostCodeViewModel(initialState, postCodeRepository)

        //Verify after set initial state this one is set as Uninitialized
        withState(underTest) { assertTrue(it.uiState is Uninitialized) }

        //When permission is granted for the first time
        underTest.onPermissionChange(permissionState = PermissionState.Granted)

        //Verify repository is called
        verify(postCodeRepository).fetchPostCode()

        //Verify the state is Success, and the value of the UI model
        withState(underTest)
        {
            assertTrue(it.uiState is Success)
            assertEquals(it.uiState.invoke(), locationUiModel)
        }
        withState(underTest) { assertFalse(it.uiState is Loading) }
    }

    @Test
    fun `should evolve in error state if something bad is happening in the network layer`() {
        val initialState = LocationUiState()

        underTest = PostCodeViewModel(initialState, postCodeRepository)

        withState(underTest) { assertTrue(it.uiState is Uninitialized) }
        whenFetchRepositoryThenReturn(Single.error(Throwable("Network error")))

        underTest.requestLocation()

        verify(postCodeRepository).fetchPostCode()

        withState(underTest)
        {
            assertTrue(it.uiState is Fail)
        }
        withState(underTest) { assertFalse(it.uiState is Loading) }
    }

    private fun whenFetchRepositoryThenReturn(toReturn: Single<LocationUiModel>) {
        given(postCodeRepository.fetchPostCode()).willReturn(toReturn)
    }
}