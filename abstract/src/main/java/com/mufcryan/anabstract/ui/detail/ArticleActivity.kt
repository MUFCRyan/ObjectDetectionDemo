package com.mufcryan.anabstract.ui.detail

import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.mufcryan.anabstract.R
import com.mufcryan.anabstract.common.constants.ExtraKeys
import com.mufcryan.anabstract.common.ui.AbstractWordCloudView
import com.mufcryan.anabstract.viewmodel.AbstractViewModel
import com.mufcryan.base.ui.BaseActivity
import com.mufcryan.base.ui.ExceptionPageView
import com.mufcryan.base.ui.LoadingView

class ArticleActivity : BaseActivity() {
  private var id = ""
  private lateinit var viewModel: AbstractViewModel
  private lateinit var ivCover: ImageView
  private lateinit var tvTitle: TextView
  private lateinit var tvContent: TextView
  private lateinit var tvDate: TextView
  private lateinit var viewAbstractWordCloud: AbstractWordCloudView

  override fun getLayoutResId() = R.layout.activity_article

  override fun initView() {
    super.initView()
    id = intent.getStringExtra(ExtraKeys.EXTRA_ID) ?: ""
    viewModel = ViewModelProvider.NewInstanceFactory().create(AbstractViewModel::class.java)

    ivCover = findViewById(R.id.iv_cover)
    tvTitle = findViewById(R.id.tv_title)
    tvContent = findViewById(R.id.tv_content)
    tvDate = findViewById(R.id.tv_date)
    viewAbstractWordCloud = findViewById(R.id.view_abstract_word_cloud)

    loadingView?.setLoadingEnable(false)
  }

  override fun initListener() {
    super.initListener()
    viewModel.article.observe(this, {
      if(it.isSuccessful){
        it.data.let { article ->
          if(!TextUtils.isEmpty(article.image)){
            Glide.with(ivCover)
              .load(article.image)
              .centerCrop()
              .into(ivCover)
          }
          tvTitle.text = article.title
          tvContent.text = article.content
          tvDate.text = article.date
          article.summary?.let { summary ->
            viewAbstractWordCloud.setData(summary.abstract, summary.wordCloud)
          }
        }
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