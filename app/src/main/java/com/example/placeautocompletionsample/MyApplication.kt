package com.example.placeautocompletionsample

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
    }
}