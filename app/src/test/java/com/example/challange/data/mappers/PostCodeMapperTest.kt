package com.example.challange.data.mappers

import com.example.challange.base.TestBase
import com.example.challange.data.model.Response
import com.example.challange.features.uimodels.Coordinates
import com.example.challange.features.uimodels.INVALID_POSTCODE
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PostCodeMapperTest : TestBase() {


    private lateinit var underTest: PostCodeMapper
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

    private val coordinates = Coordinates(1.toDouble(), 1.toDouble())

    @Before
    fun setUp() {
        underTest = PostCodeMapper()
    }

    @Test
    fun `should map correctly if response has list value`() {
        val mappedValue = underTest.map(coordinates, stubResponseSuccess!!)

        assertEquals(mappedValue.coordinates.latitude, 1.toDouble())
        assertEquals(mappedValue.coordinates.longitude, 1.toDouble())
        assertEquals(
            mappedValue.postCode.postCodeValue,
            stubResponseSuccess!!.result!!.first().postcode
        )
    }

    @Test
    fun `should map correctly if response has no items`() {
        val mappedValue = underTest.map(coordinates, stubResponseFailure!!)

        assertEquals(mappedValue.coordinates.latitude, 1.toDouble())
        assertEquals(mappedValue.coordinates.longitude, 1.toDouble())
        assertEquals(
            mappedValue.postCode.postCodeValue,
            INVALID_POSTCODE
        )
    }

}