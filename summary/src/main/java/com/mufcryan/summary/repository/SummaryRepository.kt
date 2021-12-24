package com.mufcryan.summary.repository

import com.mufcryan.summary.common.bean.SummaryBean
import com.mufcryan.base.BaseRepository
import com.mufcryan.base.bean.BaseResponse
import com.mufcryan.net.HostType
import com.mufcryan.net.NetManager
import com.mufcryan.summary.net.AbstractApi
import com.mufcryan.util.GsonUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import java.net.URLEncoder

class SummaryRepository: BaseRepository<String, BaseResponse<SummaryBean>>() {
  companion object {
    const val TEST_JSON = "{\n" +
        "    \"data\": {\n" +
        "        \"abstract\": \"宁夏始终将节能工作作为优化经济结构、加快生态文明建设的重要抓手，经济发展、社会进步与绿色低碳、循环发展逐渐展现了相互促进的良性发展轨迹。\",\n" +
        "        \"wordCloud\": [\n" +
        "            {\n" +
        "                \"word\": \"宁夏\",\n" +
        "                \"weight\": 5\n" +
        "            },\n" +
        "            {\n" +
        "                \"word\": \"发展\",\n" +
        "                \"weight\": 1\n" +
        "            },\n" +
        "            {\n" +
        "                \"word\": \"全国\",\n" +
        "                \"weight\": 1.5\n" +
        "            },\n" +
        "            {\n" +
        "                \"word\": \"检验\",\n" +
        "                \"weight\": 2.3\n" +
        "            },\n" +
        "            {\n" +
        "                \"word\": \"工作\",\n" +
        "                \"weight\": 3.5\n" +
        "            },\n" +
        "            {\n" +
        "                \"word\": \"优化\",\n" +
        "                \"weight\": 4\n" +
        "            },\n" +
        "            {\n" +
        "                \"word\": \"文明\",\n" +
        "                \"weight\": 3.1\n" +
        "            },\n" +
        "            {\n" +
        "                \"word\": \"经济\",\n" +
        "                \"weight\": 4.5\n" +
        "            },\n" +
        "            {\n" +
        "                \"word\": \"抓手\",\n" +
        "                \"weight\": 1.8\n" +
        "            },\n" +
        "            {\n" +
        "                \"word\": \"进步\",\n" +
        "                \"weight\": 2\n" +
        "            },\n" +
        "            {\n" +
        "                \"word\": \"绿色\",\n" +
        "                \"weight\": 3.6\n" +
        "            },\n" +
        "            {\n" +
        "                \"word\": \"良性\",\n" +
        "                \"weight\": 4.2\n" +
        "            }\n" +
        "        ]\n" +
        "    },\n" +
        "    \"status\": 200,\n" +
        "    \"msg\": \"OK\"\n" +
        "}"
  }

  override fun getStrategy() = DataStrategy.NET_GET_ONLY

  override fun getNetData(
    param: String?,
    callBack: RepositoryCallback<BaseResponse<SummaryBean>>?
  ) {
    /*val type = object : TypeToken<BaseResponse<SummaryBean>>() {}.type
    val response = GsonUtil.getGsonObj<BaseResponse<SummaryBean>>(TEST_JSON, type)
    callBack?.onSuccess(response)*/

    param?.let {
      val map = LinkedHashMap<String, Any>()
      map["text"] = URLEncoder.encode(it, "UTF-8")
      val body = RequestBody.create("application/json; charset=utf-8".toMediaType(),
          GsonUtil.getJsonString(map))
      NetManager.getApi(HostType.Abstract, AbstractApi::class.java)
        .getSummary(body)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe({ response ->
          if(response.isSuccessful){
            callBack?.onSuccess(response)
          } else {
            callBack?.onFailure(response.status, response.msg)
          }
        }, { throwable ->
          callBack?.onError(throwable)
        })
    }
  }
}