package com.mufcryan.summary.common.bean

import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

class ArticleBean {
  var id = ""
  var title = ""
  var content = ""
  var date = 0L
  private var dateString = ""
  var image = ""
  var summary: SummaryBean? = null

  private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.US)
  fun getFormatData(): String{
    if (!TextUtils.isEmpty(dateString)) {
      return dateString
    }
    synchronized(this){
      return DATE_FORMAT.format(Date(date))
    }
  }
}