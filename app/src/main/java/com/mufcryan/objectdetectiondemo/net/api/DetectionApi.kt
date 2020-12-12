package com.mufcryan.objectdetectiondemo.net.api

import com.mufcryan.objectdetectiondemo.base.BaseResponse
import com.mufcryan.objectdetectiondemo.bean.DetectionResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
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