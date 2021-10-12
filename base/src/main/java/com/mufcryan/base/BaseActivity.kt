package com.mufcryan.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity


abstract class BaseActivity: FragmentActivity() {
    protected abstract fun getLayoutResId(): Int
    protected open fun initView(){}
    protected open fun initListener(){}
    protected open fun initData(){}
    protected open fun isFullScreen() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(isFullScreen()){
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(getLayoutResId())
        initView()
        initListener()
        initData()
    }

    protected fun openActivity(clazz: Class<Activity>){
        val intent = Intent(this, clazz)
        startActivity(intent)
    }
}