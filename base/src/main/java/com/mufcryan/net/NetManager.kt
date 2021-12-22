package com.mufcryan.net

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object NetManager {
    private val apiCache = HashMap<Any, Any>()

    fun <T: Any> getApi(apiClass: Class<T>): T {
        return getApi(HostType.ObjectDetect, apiClass)
    }

    fun <T: Any> getApi(hostType: HostType, apiClass: Class<T>): T {
        val create = if(apiCache.containsKey(apiClass) && apiCache[apiClass] != null){
            apiCache[apiClass]
        } else {
            getRetrofitClient(hostType.getHost()).create(apiClass)
        }
        apiCache[apiClass] = create!!
        return create as T
    }

    private fun getRetrofitClient(hostUrl: String = "") = RetrofitClient.getInstance()
        .getDefaultRetrofit(hostUrl)

    private fun getHttpBuilder(): OkHttpClient{
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(10, TimeUnit.SECONDS)
        builder.writeTimeout(30, TimeUnit.SECONDS)
        builder.readTimeout(30, TimeUnit.SECONDS)
        return builder.build()
    }
}