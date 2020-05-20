package com.example.challange

import android.app.Application
import com.example.challange.injection.networkModule
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

open class ChallengeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
        startKoin {
            androidContext(this@ChallengeApplication)
            modules(listOf(networkModule))
        }
        RxJavaPlugins.setErrorHandler { throwable: Throwable ->

        }
    }

    companion object {
        private var app: ChallengeApplication? = null

        fun getApp() = app!!
        const val TIMEOUT = 10L
        const val SHARED_PREFERENCES_NAME = "prefs"
        const val END_POINT = "https://api.postcodes.io/"
    }
}