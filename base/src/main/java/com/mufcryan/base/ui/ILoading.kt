package com.mufcryan.base.ui

interface ILoading {
  fun startLoading()

  fun endLoading()

  fun setOnStartLoadingListener(listener: Runnable?)

  fun setLoadingEnable(enable: Boolean){}
}