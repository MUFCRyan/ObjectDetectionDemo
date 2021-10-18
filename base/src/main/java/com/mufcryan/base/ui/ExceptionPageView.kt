package com.mufcryan.base.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.mufcryan.base.R

class ExceptionPageView: FrameLayout {
  constructor(context: Context): this(context, null)
  constructor(context: Context, attributeSet: AttributeSet?): this(context, attributeSet, 0)
  constructor(context: Context, attributeSet: AttributeSet?, defaultStyle: Int): super(context, attributeSet, defaultStyle)
  private var pageType = PageType.NONE
  private var ivTip: ImageView
  private var tvTip: TextView
  init {
    LayoutInflater.from(context).inflate(R.layout.view_exception_page, this)
    ivTip = findViewById(R.id.iv_tip)
    tvTip = findViewById(R.id.tv_tip)
    updateByPageType()

    initListener()
  }

  private fun initListener() {
    tvTip.setOnClickListener {
      if(pageType == PageType.ERROR){
        onExceptionBtnClickListener?.onClick(it)
      }
    }
  }

  private fun updateByPageType() {
    when(pageType){
      PageType.NONE -> {
        visibility = View.GONE
      }
      PageType.ERROR -> {
        tvTip.text = "网络错误，请稍后重试"
        visibility = View.VISIBLE
      }
      PageType.EMPTY ->{
        tvTip.text = "没有数据啦，过会儿再来看看吧~"
        visibility = View.VISIBLE
      }
    }
  }

  fun setPageType(type: PageType){
    pageType = type
    updateByPageType()
  }

  private var onExceptionBtnClickListener: OnClickListener? = null
  fun setOnExceptionBtnClickListener(listener: OnClickListener?){
    onExceptionBtnClickListener = listener
  }

  enum class PageType{
    NONE, ERROR, EMPTY
  }
}