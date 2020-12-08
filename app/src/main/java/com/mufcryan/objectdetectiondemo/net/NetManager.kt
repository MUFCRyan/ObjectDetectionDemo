package com.mufcryan.objectdetectiondemo.net

import com.mufcryan.objectdetectiondemo.net.api.DetectionApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
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

    private fun getRetrofitClient()=Retrofit.Builder().client(getHttpBuilder())
        .baseUrl("")
        .build()

    private fun getHttpBuilder(): OkHttpClient{
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(10, TimeUnit.SECONDS)
        builder.writeTimeout(30, TimeUnit.SECONDS)
        builder.readTimeout(30, TimeUnit.SECONDS)
        return builder.build()
    }
}