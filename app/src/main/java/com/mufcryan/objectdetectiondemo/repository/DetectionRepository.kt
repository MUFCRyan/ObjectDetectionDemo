package com.mufcryan.objectdetectiondemo.repository

import com.mufcryan.base.BaseRepository
import com.mufcryan.base.BaseResponse
import com.mufcryan.objectdetectiondemo.bean.DetectionRequest
import com.mufcryan.objectdetectiondemo.bean.DetectionResponse
import com.mufcryan.objectdetectiondemo.net.NetManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class DetectionRepository: BaseRepository<DetectionRequest, BaseResponse<DetectionResponse>>() {
    override fun getStrategy() = DataStrategy.NET_GET_ONLY

    override fun getNetData(param: DetectionRequest?, callBack: RepositoryCallback<BaseResponse<DetectionResponse>>?) {
        super.getNetData(param, callBack)
        param?.let {
            val parameters: HashMap<String, Any> = getParameters()
            // create RequestBody instance from file
            val parts = ArrayList<MultipartBody.Part>()
            val file = File(param.filePath)
            val requestFile: RequestBody = RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                file
            )
            val body = MultipartBody.Part.createFormData("image_file", file.name, requestFile)
            parts.add(body)
            NetManager.getDetectionApi()
                .uploadPicture(HashMap(), parameters, parts)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if(it.isSuccessful){
                        callBack?.onSuccess(it)
                    } else {
                        callBack?.onFailure(it.status, it.msg)
                    }
                }, {
                    callBack?.onError(it)
                })
        }
    }

    private fun getParameters(): HashMap<String, Any> {
        val finalParams = HashMap<String, Any>()
        val builder = StringBuilder()
        finalParams["file_metas"] = convertToRequestBody(builder.toString())
        finalParams["file_type"] = convertToRequestBody("pic")
        return finalParams
    }

    private fun convertToRequestBody(param: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), param)
    }
}