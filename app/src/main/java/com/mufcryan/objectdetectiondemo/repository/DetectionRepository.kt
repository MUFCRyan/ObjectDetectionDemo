package com.mufcryan.objectdetectiondemo.repository

import com.mufcryan.objectdetectiondemo.base.BaseRepository
import com.mufcryan.objectdetectiondemo.base.BaseResponse
import com.mufcryan.objectdetectiondemo.bean.DetectionRequest
import com.mufcryan.objectdetectiondemo.bean.DetectionResponse
import com.mufcryan.objectdetectiondemo.net.NetManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DetectionRepository: BaseRepository<DetectionRequest, BaseResponse<DetectionResponse>>() {
    override fun getStrategy() = DataStrategy.NET_GET_ONLY

    override fun getNetData(param: DetectionRequest?, callBack: RepositoryCallback<BaseResponse<DetectionResponse>>?) {
        super.getNetData(param, callBack)
        param?.let {
            /*NetManager.getDetectionApi()
            .uploadPicture()
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
            })*/
        }
    }
}