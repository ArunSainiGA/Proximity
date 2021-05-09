package com.asp.proximitylabs.di

import android.app.Application
import com.asp.proximitylabs.utils.WEB_SOCKET_URI
import com.asp.proximitylabs.data.client.AirQualitySocketClient

object DependencyInjector{

    fun init(application: Application){
        // Initialize dependencies
    }

    fun getAirQualityClient() = AirQualitySocketClient(WEB_SOCKET_URI)
}