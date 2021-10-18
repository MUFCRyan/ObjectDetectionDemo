package com.mufcryan.base.ui

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class BasePagingActivity<Data, Holder: RecyclerView.ViewHolder>: BaseActivity() {
  private lateinit var recyclerView: RecyclerView

  private lateinit var adapter: BaseAdapter<Data, Holder>
  var canLoadMore = true
  override fun initView() {
    super.initView()
    recyclerView = provideRecyclerView()
  }

  override fun initListener() {
    super.initListener()
    val layoutManager = provideLayoutManager(this)
    recyclerView.layoutManager = layoutManager
    adapter = provideRecyclerAdapter()
    recyclerView.adapter = adapter
    recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
      private var state = RecyclerView.SCROLL_STATE_IDLE
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        state = newState
        if(state == RecyclerView.SCROLL_STATE_IDLE
          && adapter.list.isNotEmpty()
          && layoutManager.findLastCompletelyVisibleItemPosition() == adapter.list.size - 1
          && canLoadMore){
          onLoadMore()
        }
      }
    })
  }

  abstract fun provideRecyclerView(): RecyclerView

  abstract fun provideRecyclerAdapter(): BaseAdapter<Data, Holder>

  protected open fun provideLayoutManager(context: Context) = LinearLayoutManager(context)

  fun hasData() = adapter.list.isNotEmpty()

  override fun onReloadData() {
    super.onReloadData()
    onRefresh()
  }

  protected open fun onRefresh(){
    canLoadMore = true
  }

  protected open fun onLoadMore(){}

  protected fun onRefreshSucceed(list: List<Data>){
    adapter.setList(list)
    if(adapter.list.isEmpty()){
      onShowEmptyPage()
    }
    onEndLoadData()
  }

  protected fun onRefreshFailure(){
    if(adapter.list.isEmpty()){
      onShowErrorPage()
    } else {
      showErrorToast()
    }
    onEndLoadData()
  }

  protected fun onLoadMoreSucceed(list: List<Data>){
    adapter.addList(list)
    onEndLoadData()
  }

  protected fun onLoadMoreFailure(){
    showErrorToast()
    onEndLoadData()
  }
}