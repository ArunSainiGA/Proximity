package com.asp.proximitylabs

import android.app.Application
import com.asp.proximitylabs.di.DependencyInjector

class ProximityApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        DependencyInjector.init(this)
    }
}