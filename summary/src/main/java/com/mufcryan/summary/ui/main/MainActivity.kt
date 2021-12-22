package com.mufcryan.summary.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.mufcryan.summary.R
import com.mufcryan.summary.common.bean.ArticleBean
import com.mufcryan.summary.common.constants.ExtraKeys
import com.mufcryan.summary.common.ui.ArticleHolder
import com.mufcryan.summary.common.ui.ArticleListAdapter
import com.mufcryan.summary.ui.user_input.UserInputActivity
import com.mufcryan.summary.viewmodel.AbstractViewModel
import com.mufcryan.base.ui.BaseAdapter
import com.mufcryan.base.ui.BasePagingActivity
import com.mufcryan.base.ui.ExceptionPageView
import com.mufcryan.base.ui.LoadingView
import com.mufcryan.base.ui.SearchBar

class MainActivity : BasePagingActivity<ArticleBean, ArticleHolder>() {
  private lateinit var tvTitle: TextView
  private lateinit var tvInputByUser: TextView
  private lateinit var searchBar: SearchBar
  private lateinit var viewModel: AbstractViewModel
  private var nextPageNumber = 0
  private var pageType = PageType.MAIN

  override fun getLayoutResId() = R.layout.activity_main

  override fun initView() {
    super.initView()
    intent?.let {
      if (it.getSerializableExtra(ExtraKeys.EXTRA_PAGE_TYPE) is PageType) {
        pageType = it.getSerializableExtra(ExtraKeys.EXTRA_PAGE_TYPE) as PageType
      }
    }
    viewModel = ViewModelProvider.NewInstanceFactory().create(AbstractViewModel::class.java)
    tvTitle = findViewById(R.id.tv_title)
    tvInputByUser = findViewById(R.id.tv_user_input)
    if(pageType == PageType.SEARCH){
      tvInputByUser.visibility = View.GONE
    }
    searchBar = findViewById(R.id.search_bar)
  }

  override fun initListener() {
    super.initListener()
    if (pageType == PageType.MAIN) {
      tvInputByUser.setOnClickListener {
        openActivity(UserInputActivity::class.java)
      }
      searchBar.setOnSearchClickListener { v ->
        val bundle = Bundle()
        bundle.putSerializable(ExtraKeys.EXTRA_PAGE_TYPE, PageType.SEARCH)
        openActivity(MainActivity::class.java, bundle)
      }
    } else {
      searchBar.setOnCancelClickListener {
        finish()
      }
      searchBar.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
          clearList()
          requestData()
        }
      })
    }

    viewModel.articleList.observe(this, {
      if(it.isSuccessful){
        when (pageType) {
          PageType.MAIN -> {
            if(hasData()){
              onLoadMoreSucceed(it.data.list)
            } else {
              onRefreshSucceed(it.data.list)
            }
          }

          PageType.SEARCH -> {
            if (searchBar.text.toString() == it.data.searchWord) {
              onRefreshSucceed(it.data.list)
            }
          }
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
    if (pageType == PageType.SEARCH) {
      tvTitle.text = "搜索"
      searchBar.setStyle(SearchBar.Style.TYPE_2)
      loadingView?.setLoadingEnable(false)
    }
  }

  override fun requestData() {
    super.requestData()
    if (pageType == PageType.MAIN) {
      viewModel.getArticleList(nextPageNumber)
    } else if(!TextUtils.isEmpty(searchBar.text)) {
      viewModel.getSearchArticleList(searchBar.text.toString())
    } else {
      loadingView?.setLoadingEnable(false)
    }
  }

  override fun onRefresh() {
    super.onRefresh()
    nextPageNumber = 0
    requestData()
  }

  override fun onLoadMore() {
    super.onLoadMore()
    requestData()
  }

  override fun isRequestWhenInit(): Boolean {
    return pageType == PageType.MAIN
  }

  override fun provideRecyclerView() = findViewById<RecyclerView>(R.id.recycler_view)!!

  override fun provideLoadingView(): LoadingView = findViewById(R.id.view_loading)

  override fun provideExceptionPageView(): ExceptionPageView = findViewById(R.id.view_exception_page)

  override fun provideRecyclerAdapter(): BaseAdapter<ArticleBean, ArticleHolder> {
    return ArticleListAdapter()
  }

  override fun canShowEmptyPage(): Boolean {
    return pageType == PageType.MAIN
  }

  override fun onResume() {
    super.onResume()
    searchBar.postDelayed({ searchBar.showSoftInput() }, 60)
  }

  override fun finish() {
    super.finish()
    searchBar.hideSoftInput()
  }

  enum class PageType {
    MAIN, SEARCH
  }
}