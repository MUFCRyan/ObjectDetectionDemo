package com.mufcryan.summary.common.ui

import android.Manifest
import android.view.View
import com.mufcryan.summary.R
import com.mufcryan.summary.ui.main.MainActivity
import com.mufcryan.base.ui.BaseActivity
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class SplashActivity : BaseActivity() {
  private lateinit var btnEnterArticleList: View

  override fun getLayoutResId() = R.layout.activity_splash

  override fun initView() {
    super.initView()
    btnEnterArticleList = findViewById(R.id.btn_enter_article_list)
  }

  override fun initListener() {
    super.initListener()
    btnEnterArticleList.setOnClickListener {
      openArticleListWithPermissionCheck()
    }
  }

  @NeedsPermission(Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE)
  fun openArticleList(){
    openActivity(MainActivity::class.java)
  }
}