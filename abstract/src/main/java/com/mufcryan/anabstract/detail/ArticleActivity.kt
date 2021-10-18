package com.mufcryan.anabstract.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.mufcryan.anabstract.R
import com.mufcryan.anabstract.common.constants.ExtraKeys
import com.mufcryan.anabstract.viewmodel.AbstractViewModel
import com.mufcryan.base.ui.BaseActivity
import com.mufcryan.base.ui.ExceptionPageView
import com.mufcryan.base.ui.ILoading
import com.mufcryan.base.ui.LoadingView

class ArticleActivity : BaseActivity() {
  private var id = ""
  private lateinit var viewModel: AbstractViewModel
  private lateinit var ivCover: ImageView
  private lateinit var tvTitle: TextView
  private lateinit var tvContent: TextView
  private lateinit var tvDate: TextView

  override fun getLayoutResId() = R.layout.activity_article

  override fun initView() {
    super.initView()
    id = intent.getStringExtra(ExtraKeys.EXTRA_ID) ?: ""
    viewModel = ViewModelProvider.NewInstanceFactory().create(AbstractViewModel::class.java)

    ivCover = findViewById(R.id.iv_cover)
    tvTitle = findViewById(R.id.tv_title)
    tvContent = findViewById(R.id.tv_content)
    tvDate = findViewById(R.id.tv_date)

    loadingView?.setLoadingEnable(false)
  }

  override fun initListener() {
    super.initListener()
    viewModel.article.observe(this, {
      if(it.isSuccessful){
        if(!TextUtils.isEmpty(it.data.image)){
          Glide.with(ivCover)
            .load(it.data.image)
            .centerCrop()
            .into(ivCover)
        }
        tvTitle.text = it.data.title
        tvContent.text = it.data.content
        tvDate.text = it.data.date
      } else {
        onShowErrorPage()
      }

      onEndLoadData()
    })
  }

  override fun requestData(){
    super.requestData()
    viewModel.getArticle(id)
  }

  override fun provideLoadingView(): LoadingView = findViewById(R.id.view_loading)

  override fun provideExceptionPageView(): ExceptionPageView = findViewById(R.id.view_exception_page)
}