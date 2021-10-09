package com.mufcryan.objectdetectiondemo.net.api

import com.mufcryan.base.BaseResponse
import com.mufcryan.objectdetectiondemo.bean.DetectionResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

interface DetectionApi {
    @JvmSuppressWildcards
    @Multipart
    @POST("/api/v1/upload_image")
    fun uploadPicture(
        @HeaderMap headers: Map<String, Any>,
        @QueryMap params: HashMap<String, Any>,
        @Part file: List<MultipartBody.Part>
    ): Observable<BaseResponse<DetectionResponse>>
}