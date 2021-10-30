package com.mufcryan.summary.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mufcryan.summary.common.bean.ArticleBean
import com.mufcryan.summary.common.bean.ArticlePagingBean
import com.mufcryan.summary.common.bean.SummaryBean
import com.mufcryan.summary.repository.SummaryRepository
import com.mufcryan.summary.repository.ArticleListRepository
import com.mufcryan.summary.repository.ArticleRepository
import com.mufcryan.summary.repository.SearchArticleListRepository
import com.mufcryan.summary.common.bean.SearchPagingRequest
import com.mufcryan.base.bean.BasePagingRequest
import com.mufcryan.base.bean.BaseResponse

class AbstractViewModel: ViewModel() {
  val articleList = MutableLiveData<BaseResponse<ArticlePagingBean>>()
  private val articleListRepository = ArticleListRepository()
  fun getArticleList(pageNumber: Int = 0, pageSize: Int = 20){
    val request = BasePagingRequest(pageNumber, pageSize)
    articleListRepository.processDataForResponse(request, articleList)
  }

  private val searchArticleListRepository = SearchArticleListRepository()
  fun getSearchArticleList(searchWord: String, pageNumber: Int = 0, pageSize: Int = 20){
    val request = SearchPagingRequest(searchWord, pageNumber, pageSize)
    searchArticleListRepository.processDataForResponse(request, articleList)
  }

  val article = MutableLiveData<BaseResponse<ArticleBean>>()
  private val articleRepository = ArticleRepository()
  fun getArticle(id: String){
    articleRepository.processDataForResponse(id, article)
  }

  val summary = MutableLiveData<BaseResponse<SummaryBean>>()
  private val summaryRepository = SummaryRepository()
  fun getSummary(input: String){
    summaryRepository.processDataForResponse(input, summary)
  }
}