package com.mufcryan.summary.net

import com.mufcryan.summary.common.bean.ArticleBean
import com.mufcryan.summary.common.bean.ArticlePagingBean
import com.mufcryan.summary.common.bean.SummaryBean
import com.mufcryan.base.bean.BaseResponse
import retrofit2.http.GET
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AbstractApi {
  @JvmSuppressWildcards
  @GET("/dashboard/list")
  fun getArticleList(@Query("pageNumber") pageNumber: Int = 0, @Query("pageSize") pageSize: Int = 20): Observable<BaseResponse<ArticlePagingBean>>

  @JvmSuppressWildcards
  @GET("/dashboard/list")
  fun getSearchArticleList(
      @Query("searchWord") searchWord: String,
      @Query("pageNumber") pageNumber: Int = 0,
      @Query("pageSize") pageSize: Int = 20): Observable<BaseResponse<ArticlePagingBean>>

  @JvmSuppressWildcards
  @GET("/dashboard/detail")
  fun getArticle(@Query("id") id: String): Observable<BaseResponse<ArticleBean>>

  @JvmSuppressWildcards
  @POST("/dashboard/summary")
  fun getSummary(@Body body: RequestBody): Observable<BaseResponse<SummaryBean>>
}