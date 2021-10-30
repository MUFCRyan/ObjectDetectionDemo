package com.mufcryan.anabstract.ui.user_input

import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.mufcryan.anabstract.R
import com.mufcryan.anabstract.common.ui.SummaryView
import com.mufcryan.anabstract.viewmodel.AbstractViewModel
import com.mufcryan.base.ui.BaseActivity
import com.mufcryan.base.ui.ILoading
import com.mufcryan.base.ui.LoadingView

class UserInputActivity : BaseActivity() {
  private lateinit var etUserInput: EditText
  private lateinit var tvGenerate: TextView
  private lateinit var summaryView: SummaryView
  private lateinit var viewModel: AbstractViewModel

  override fun getLayoutResId() = R.layout.activity_user_input

  override fun initView() {
    super.initView()
    viewModel = ViewModelProvider.NewInstanceFactory().create(AbstractViewModel::class.java)
    etUserInput = findViewById(R.id.et_user_input)
    tvGenerate = findViewById(R.id.tv_generate)
    summaryView = findViewById(R.id.view_summary)
    loadingView?.setLoadingEnable(false)
  }

  override fun initListener() {
    super.initListener()
    tvGenerate.setOnClickListener {
      if (!TextUtils.isEmpty(etUserInput.text)) {
        requestData()
      } else {
        Toast.makeText(this, "请先输入文本", Toast.LENGTH_SHORT).show()
      }
    }

    viewModel.summary.observe(this, {
      if (it.isSuccessful) {
        summaryView.setData(it.data)
      } else {
        showErrorToast()
      }
    })
  }

  override fun requestData() {
    super.requestData()
    viewModel.getSummary(etUserInput.text.toString())
  }

  override fun isRequestWhenInit(): Boolean {
    return false
  }

  override fun provideLoadingView(): ILoading? {
    return findViewById<LoadingView>(R.id.view_loading)
  }
}