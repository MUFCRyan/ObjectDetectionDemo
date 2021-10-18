package com.mufcryan.base.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.fragment.app.FragmentActivity


abstract class BaseActivity: FragmentActivity() {
    private var exceptionPageView: ExceptionPageView? = null
    private var loadingView: ILoading? = null

    protected abstract fun getLayoutResId(): Int

    @CallSuper
    protected open fun initView(){
        exceptionPageView = provideExceptionPageView()
        loadingView = provideLoadingView()
    }

    @CallSuper
    protected open fun initListener(){
        exceptionPageView?.let {
            it.setOnExceptionBtnClickListener {
                onReloadData()
            }
        }

        loadingView?.setOnStartLoadingListener {
            onReloadData()
            loadingView?.startLoading()
        }
    }
    protected open fun initData(){}
    protected open fun isFullScreen() = false

    protected open fun onReloadData(){
        exceptionPageView?.setPageType(ExceptionPageView.PageType.NONE)
        loadingView?.startLoading()
    }

    @CallSuper
    protected open fun onEndLoadData(){
        loadingView?.endLoading()
    }

    protected open fun provideExceptionPageView(): ExceptionPageView? = null

    protected open fun provideLoadingView(): ILoading? = null

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

    protected fun openActivity(clazz: Class<out Activity>){
        val intent = Intent(this, clazz)
        startActivity(intent)
    }

    @CallSuper
    protected fun onShowErrorPage(){
        exceptionPageView?.setPageType(ExceptionPageView.PageType.ERROR)
    }

    @CallSuper
    protected fun onShowEmptyPage(){
        exceptionPageView?.setPageType(ExceptionPageView.PageType.EMPTY)
    }

    fun showErrorToast(){
        Toast.makeText(this, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show()
    }
}