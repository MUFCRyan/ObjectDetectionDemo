package com.mufcryan.anabstract.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mufcryan.anabstract.common.bean.ArticleBean
import com.mufcryan.anabstract.common.bean.ArticlePagingBean
import com.mufcryan.anabstract.repository.ArticleListRepository
import com.mufcryan.anabstract.repository.ArticleRepository
import com.mufcryan.base.bean.BasePagingRequest
import com.mufcryan.base.bean.BaseResponse

class AbstractViewModel: ViewModel() {
  val articleList = MutableLiveData<BaseResponse<ArticlePagingBean>>()
  private val articleListRepository = ArticleListRepository()
  fun getArticleList(pageNumber: Int = 0, pageSize: Int = 20){
    val request = BasePagingRequest(pageNumber, pageSize)
    articleListRepository.processDataForResponse(request, articleList)
  }

  val article = MutableLiveData<BaseResponse<ArticleBean>>()
  private val articleRepository = ArticleRepository()
  fun getArticle(id: String){
    articleRepository.processDataForResponse(id, article)
  }
}