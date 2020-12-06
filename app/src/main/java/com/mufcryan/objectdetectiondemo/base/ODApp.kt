package com.mufcryan.objectdetectiondemo.base

import android.app.Application
import android.content.Context

class ODApp: Application() {
    companion object {
        lateinit var context: Context
        lateinit var appExecutors: AppExecutors
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        appExecutors = AppExecutors.getInstance()
    }
}