package com.mufcryan.summary.common.bean

import com.mufcryan.base.bean.BasePagingRequest

class SearchPagingRequest(var searchWord: String, pageNumber: Int = 0, pageSize: Int = 20):
  BasePagingRequest(pageNumber, pageSize) {
}