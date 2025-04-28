package com.listenergao.kotlinsample.android

import android.app.Application

class KotlinApplication : Application() {

    companion object {
        @JvmStatic
        lateinit var application: Application
    }


    override fun onCreate() {
        super.onCreate()

        application = this

    }
}