package com.example.challange.base

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
abstract class TestBase {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
}