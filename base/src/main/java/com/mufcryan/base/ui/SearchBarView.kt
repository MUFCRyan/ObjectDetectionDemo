package com.mufcryan.base.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class SearchBarView: FrameLayout {
  constructor(context: Context): this(context, null)
  constructor(context: Context, attributeSet: AttributeSet?): this(context, attributeSet, 0)
  constructor(context: Context, attributeSet: AttributeSet?, defaultStyle: Int): super(context, attributeSet, defaultStyle)
}