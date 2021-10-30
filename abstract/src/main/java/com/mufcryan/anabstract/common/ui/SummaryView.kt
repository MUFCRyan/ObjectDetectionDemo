package com.mufcryan.anabstract.common.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.mufcryan.anabstract.R
import com.mufcryan.anabstract.common.bean.SummaryBean
import com.mufcryan.anabstract.common.bean.WordCloudBean
import com.mufcryan.util.DisplayUtil

class SummaryView: LinearLayout {
  private var tvAbstract: TextView
  private var viewWordCloud: WordCloudView

  constructor(context: Context): this(context, null)
  constructor(context: Context, attributeSet: AttributeSet?): this(context, attributeSet, 0)
  constructor(context: Context, attributeSet: AttributeSet?, defaultStyle: Int): super(context, attributeSet, defaultStyle)

  init {
    orientation = VERTICAL
    val paddingHorizontal = DisplayUtil.dp2Px(context, 14f)
    val paddingVertical = DisplayUtil.dp2Px(context, 10f)
    setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
    LayoutInflater.from(context).inflate(R.layout.view_summary, this, true)
    tvAbstract = findViewById(R.id.tv_abstract)
    viewWordCloud = findViewById(R.id.view_word_cloud)
  }

  fun setData(summary: SummaryBean){
    tvAbstract.text = summary.abstract
    post {
      viewWordCloud.setData(summary.wordCloud)
    }
  }
}