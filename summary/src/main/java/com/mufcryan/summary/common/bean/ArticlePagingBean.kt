package com.mufcryan.summary.common.bean

import com.mufcryan.base.bean.BasePagingBean

class ArticlePagingBean: BasePagingBean<ArticleBean>() {
  var searchWord = ""
}