package com.mufcryan.anabstract.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mufcryan.anabstract.common.bean.ArticlePagingBean
import com.mufcryan.anabstract.repository.ArticleListRepository
import com.mufcryan.base.bean.BasePagingRequest
import com.mufcryan.base.bean.BaseResponse

class AbstractViewModel: ViewModel() {
  val articleList = MutableLiveData<BaseResponse<ArticlePagingBean>>()
  private val articleListRepository = ArticleListRepository()
  fun getArticleList(pageNumber: Int = 0, pageSize: Int = 20){
    val request = BasePagingRequest(pageNumber, pageSize)
    articleListRepository.processDataForResponse(request, articleList)
  }
}