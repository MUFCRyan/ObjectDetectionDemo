package com.mufcryan.summary.common.bean

class WordCloudBean: Comparable<WordCloudBean> {
  var word = ""
  var weight = 1.0F
  override fun compareTo(other: WordCloudBean) = when{
    weight > other.weight -> 1
    weight == other.weight -> 0
    else -> -1
  }
}