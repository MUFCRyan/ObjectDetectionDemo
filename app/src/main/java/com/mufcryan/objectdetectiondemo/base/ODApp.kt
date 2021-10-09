package com.mufcryan.objectdetectiondemo.base

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import com.mufcryan.BaseApp
import com.mufcryan.base.AppExecutors

class ODApp: Application() {
    companion object {
        lateinit var context: Context
        lateinit var appExecutors: AppExecutors
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        BaseApp.context = base
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        appExecutors = AppExecutors.getInstance()
        Stetho.initializeWithDefaults(this)
    }
}