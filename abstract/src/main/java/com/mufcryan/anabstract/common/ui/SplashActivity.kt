package com.mufcryan.anabstract.common.ui

import android.Manifest
import android.view.View
import com.mufcryan.anabstract.R
import com.mufcryan.anabstract.main.MainActivity
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

  @NeedsPermission(Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
  fun openArticleList(){
    openActivity(MainActivity::class.java)
  }
}