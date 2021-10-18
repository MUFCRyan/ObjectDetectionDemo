package com.mufcryan.base.ui

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class LoadingView: SwipeRefreshLayout, ILoading {
  constructor(context: Context): this(context, null)
  constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet)

  init {
    setOnRefreshListener {
      onStartLoadingListener?.run()
    }
  }

  override fun startLoading() {
    isRefreshing = true
  }

  override fun endLoading() {
    isRefreshing = false
  }

  private var onStartLoadingListener: Runnable? = null
  override fun setOnStartLoadingListener(listener: Runnable?) {
    onStartLoadingListener = listener
  }

  override fun setLoadingEnable(enable: Boolean) {
    isEnabled = enable
  }
}