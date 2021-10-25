package com.mufcryan.anabstract.common.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.mufcryan.anabstract.R
import com.mufcryan.anabstract.common.bean.ArticleBean
import com.mufcryan.anabstract.common.constants.ExtraKeys
import com.mufcryan.anabstract.ui.detail.ArticleActivity
import com.mufcryan.base.ui.BaseActivity
import com.mufcryan.base.ui.BaseAdapter

class ArticleListAdapter: BaseAdapter<ArticleBean, ArticleHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
    return ArticleHolder(view)
  }

  @SuppressLint("CheckResult")
  override fun onBindViewHolder(holder: ArticleHolder, position: Int) {
    if (position in list.indices){
      val data = list[position]
      if(!TextUtils.isEmpty(data.image)){
        Glide.with(holder.ivCover)
          .load(data.image)
          .centerCrop()
          .into(holder.ivCover)
      }
      holder.tvTitle.text = data.title
      holder.tvContent.text = data.content
      holder.tvDate.text = data.date

      holder.itemView.setOnClickListener {
        val bundle = Bundle()
        bundle.putString(ExtraKeys.EXTRA_ID, data.id)
        (it.context as BaseActivity).openActivity(ArticleActivity::class.java, bundle)
      }
    }
  }
}