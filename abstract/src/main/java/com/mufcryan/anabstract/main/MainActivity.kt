package com.mufcryan.anabstract.main

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.mufcryan.anabstract.R
import com.mufcryan.anabstract.common.bean.ArticleBean
import com.mufcryan.anabstract.common.bean.ArticlePagingBean
import com.mufcryan.anabstract.common.ui.ArticleHolder
import com.mufcryan.anabstract.common.ui.ArticleListAdapter
import com.mufcryan.anabstract.viewmodel.AbstractViewModel
import com.mufcryan.base.bean.BaseResponse
import com.mufcryan.base.ui.BaseAdapter
import com.mufcryan.base.ui.BasePagingActivity
import com.mufcryan.base.ui.SearchBar

class MainActivity : BasePagingActivity<ArticleBean, ArticleHolder>() {
  private lateinit var searchBar: SearchBar
  private lateinit var viewModel: AbstractViewModel
  private var nextPageNumber = 0

  override fun getLayoutResId() = R.layout.activity_main

  override fun initView() {
    super.initView()
    viewModel = ViewModelProvider.NewInstanceFactory().create(AbstractViewModel::class.java)
    searchBar = findViewById(R.id.search_bar)
  }

  override fun initListener() {
    super.initListener()
    searchBar.setOnSearchClickListener { v ->
      // TODO 进入搜索列表页
    }

    viewModel.articleList.observe(this, {
      if(it.isSuccessful){
        if(hasData()){
          onLoadMoreSucceed(it.data.list)
        } else {
          onRefreshSucceed(it.data.list)
        }
        canLoadMore = it.data.hasMore
        nextPageNumber = it.data.pageNumber
      } else {
        if(hasData()){
          onRefreshFailure()
        } else {
          onLoadMoreFailure()
        }
      }
    })
  }

  override fun initData() {
    super.initData()
    viewModel.getArticleList(nextPageNumber)
  }

  override fun onRefresh() {
    super.onRefresh()
    nextPageNumber = 0
  }

  override fun provideRecyclerView() = findViewById<RecyclerView>(R.id.recycler_view)!!

  override fun provideRecyclerAdapter(): BaseAdapter<ArticleBean, ArticleHolder> {
    return ArticleListAdapter()
  }
}