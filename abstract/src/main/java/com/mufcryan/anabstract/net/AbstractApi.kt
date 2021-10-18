package com.mufcryan.anabstract.net

import com.mufcryan.anabstract.common.bean.ArticlePagingBean
import com.mufcryan.base.bean.BaseResponse
import retrofit2.http.GET
import io.reactivex.Observable
import retrofit2.http.Query

interface AbstractApi {
  @JvmSuppressWildcards
  @GET("/api/v1/upload_image")
  fun getArticleList(@Query("pageNumber") pageNumber: Int = 0, @Query("pageSize") pageSize: Int = 20): Observable<BaseResponse<ArticlePagingBean>>
}