package com.mufcryan.base.bean

open class BasePagingBean<Data> {
  var list = ArrayList<Data>()
  var pageNumber = 0
  var hasMore = true
}