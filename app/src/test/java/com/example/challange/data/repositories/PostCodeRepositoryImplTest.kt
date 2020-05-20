package com.example.challange.data.repositories

import com.example.challange.base.TestBase
import com.example.challange.data.PostCodesService
import com.example.challange.data.mappers.PostCodeMapper
import com.example.challange.data.model.Response
import com.example.challange.features.uimodels.Coordinates
import com.example.challange.features.uimodels.LocationUiModel
import com.example.challange.utility.location.LocationClient
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock

class PostCodeRepositoryImplTest : TestBase() {

    @Mock
    private lateinit var locationClient: LocationClient

    @Mock
    private lateinit var mapper: PostCodeMapper

    @Mock
    private lateinit var api: PostCodesService

    @Mock
    private lateinit var locationUiModel: LocationUiModel

    private lateinit var underTest: PostCodeRepositoryImpl
    private val adapter = moshi.adapter(Response::class.java)

    private val stubResponseSuccess by lazy {
        val mockResponse =
            requireNotNull(javaClass.classLoader?.getResourceAsStream("responseSuccess.json")).bufferedReader()
                .use { it.readText() }

        return@lazy adapter.fromJson(mockResponse)
    }

    private val stubResponseFailure by lazy {
        val mockResponse =
            requireNotNull(javaClass.classLoader?.getResourceAsStream("responseNotFound.json")).bufferedReader()
                .use { it.readText() }

        return@lazy adapter.fromJson(mockResponse)
    }

    @Before
    fun setUp() {
        underTest = PostCodeRepositoryImpl(api, locationClient, mapper)
    }

    @Test
    fun `should emit error when network fails`() {
        val throwable = Throwable("Network error")

        givenLocationWillReturn(Single.error(throwable))

        underTest.fetchPostCode()
            .test()
            .assertError(throwable)
    }

    @Test
    fun `should emit the first post code on the results correctly if location is retrieved`() {
        val stubCoordinates = Coordinates(latitude = 1.toDouble(), longitude = 1.toDouble())

        givenLocationWillReturn(Single.just(stubCoordinates))
        givenPostCodeApiWillReturnPostCode(stubCoordinates, Single.just(stubResponseSuccess))
        givenMapperWillReturn(stubCoordinates, stubResponseSuccess!!, locationUiModel)

        underTest.fetchPostCode()
            .test()
            .assertOf { locationUiModel }
            .assertNoErrors()
    }

    @Test
    fun `should emit unknown postcode if result is not found`() {
        val stubCoordinates = Coordinates(latitude = 1.toDouble(), longitude = 1.toDouble())

        givenLocationWillReturn(Single.just(stubCoordinates))
        givenPostCodeApiWillReturnPostCode(stubCoordinates, Single.just(stubResponseFailure))
        givenMapperWillReturn(stubCoordinates, stubResponseFailure!!, locationUiModel)

        underTest.fetchPostCode()
            .test()
            .assertOf { locationUiModel }
            .assertNoErrors()
    }

    private fun givenLocationWillReturn(coordinatesToReturn: Single<Coordinates>) {
        given(locationClient.getLastLocation()).willReturn(coordinatesToReturn)
    }

    private fun givenPostCodeApiWillReturnPostCode(
        coordinates: Coordinates,
        responseToReturn: Single<Response>
    ) {
        given(
            api.fetchPostCode(
                lat = coordinates.latitude,
                lon = coordinates.longitude
            )
        ).willReturn(responseToReturn)
    }

    private fun givenMapperWillReturn(
        coordinates: Coordinates,
        networkResponse: Response,
        locationUiModel: LocationUiModel
    ) {
        given(mapper.map(coordinates, networkResponse)).willReturn(locationUiModel)
    }
}