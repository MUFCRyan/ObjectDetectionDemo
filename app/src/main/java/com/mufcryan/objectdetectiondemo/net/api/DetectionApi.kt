package com.mufcryan.objectdetectiondemo.net.api

import com.mufcryan.objectdetectiondemo.base.BaseResponse
import com.mufcryan.objectdetectiondemo.bean.DetectionResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

interface DetectionApi {
    @Multipart
    @POST("")
    fun uploadPicture(
        @HeaderMap headers: Map<String, Any>?,
        @QueryMap params: Map<String?, Any>?,
        @Part file: List<MultipartBody.Part>
    ): Observable<BaseResponse<DetectionResponse>>
}