package com.listenergao.kotlinsample

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