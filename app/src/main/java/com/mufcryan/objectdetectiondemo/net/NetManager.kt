package com.mufcryan.objectdetectiondemo.net

import com.mufcryan.net.RetrofitClient
import com.mufcryan.objectdetectiondemo.net.api.DetectionApi
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object NetManager {
    private var detectionApi: DetectionApi? = null
    fun getDetectionApi(): DetectionApi {
        detectionApi?.let {
            return it
        }
        detectionApi = getRetrofitClient().create(DetectionApi::class.java)
        return detectionApi!!
    }

    private fun getRetrofitClient() = RetrofitClient.getInstance().getDefaultRetrofit(null)

    private fun getHttpBuilder(): OkHttpClient{
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(10, TimeUnit.SECONDS)
        builder.writeTimeout(30, TimeUnit.SECONDS)
        builder.readTimeout(30, TimeUnit.SECONDS)
        return builder.build()
    }
}