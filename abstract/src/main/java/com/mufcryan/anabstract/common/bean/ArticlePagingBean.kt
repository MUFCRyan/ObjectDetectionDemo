package com.mufcryan.anabstract.common.bean

import com.mufcryan.base.bean.BasePagingBean

class ArticlePagingBean: BasePagingBean<ArticleBean>() {
  var searchWord = ""
}