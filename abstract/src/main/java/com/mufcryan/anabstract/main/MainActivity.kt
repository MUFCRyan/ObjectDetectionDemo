package com.mufcryan.anabstract.main

import androidx.recyclerview.widget.RecyclerView
import com.mufcryan.anabstract.R
import com.mufcryan.anabstract.common.bean.ArticleBean
import com.mufcryan.anabstract.common.ui.ArticleHolder
import com.mufcryan.base.ui.BaseAdapter
import com.mufcryan.base.ui.BasePagingActivity

class MainActivity : BasePagingActivity<ArticleBean, ArticleHolder>() {
  override fun getLayoutResId() = R.layout.activity_main

  override fun initView() {
  }

  override fun initListener() {
  }

  override fun initData() {
  }

  override fun provideRecyclerView() = findViewById<RecyclerView>(R.id.recycler_view)!!

  override fun provideRecyclerAdapter(): BaseAdapter<ArticleBean, ArticleHolder> {
    TODO("Not yet implemented")
  }
}