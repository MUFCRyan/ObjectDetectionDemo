package com.mufcryan.base.ui

import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class BasePagingActivity<Data, Holder: RecyclerView.ViewHolder>: BaseActivity() {
  private lateinit var recyclerView: RecyclerView
  private lateinit var exceptionPageView: ExceptionPageView
  private lateinit var adapter: BaseAdapter<Data, Holder>
  var canLoadMore = true
  override fun initView() {
    recyclerView = provideRecyclerView()
    exceptionPageView = provideExceptionPageView()
  }

  override fun initListener() {
    val layoutManager = provideLayoutManager(this)
    recyclerView.layoutManager = layoutManager
    adapter = provideRecyclerAdapter()
    recyclerView.adapter = adapter
    recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
      private var state = RecyclerView.SCROLL_STATE_IDLE
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        state = newState
      }

      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if(state == RecyclerView.SCROLL_STATE_IDLE
            && adapter.list.isNotEmpty()
            && layoutManager.findLastCompletelyVisibleItemPosition() == adapter.list.size - 1
            && canLoadMore){
          onLoadMore()
        }
      }
    })

    // TODO 监听 exceptionPageView 重试按钮的点击
  }

  abstract fun provideRecyclerView(): RecyclerView

  abstract fun provideExceptionPageView(): ExceptionPageView

  abstract fun provideRecyclerAdapter(): BaseAdapter<Data, Holder>

  fun provideLayoutManager(context: Context) = LinearLayoutManager(context)

  fun onRefresh(){
    canLoadMore = true
  }

  fun onLoadMore(){}

  protected fun onRefreshSucceed(list: List<Data>){
    adapter.setList(list)
    if(adapter.list.isEmpty()){
      onShowEmptyPage()
    }
  }

  protected fun onRefreshFailure(){
    if(adapter.list.isEmpty()){
      onShowErrorPage()
    } else {
      showErrorToast()
    }
  }

  protected fun onLoadMoreSucceed(list: List<Data>){
    adapter.addList(list)
  }

  protected fun onLoadMoreFailure(){
    showErrorToast()
  }

  fun showErrorToast(){
    Toast.makeText(this, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show()
  }

  protected fun onShowErrorPage(){
    exceptionPageView.setPageType(ExceptionPageView.PageType.ERROR)
  }

  protected fun onShowEmptyPage(){
    exceptionPageView.setPageType(ExceptionPageView.PageType.EMPTY)
  }
}