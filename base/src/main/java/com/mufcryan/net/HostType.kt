package com.mufcryan.net

enum class HostType {
  ObjectDetect {
    override fun getHost() = "http://a.zhipengzhao.top:8888"
  },
  Abstract {
    override fun getHost() = "https://lpservice.eqxiu.com"
  };

  abstract fun getHost(): String
}