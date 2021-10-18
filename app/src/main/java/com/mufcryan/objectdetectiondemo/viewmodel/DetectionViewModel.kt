package com.mufcryan.objectdetectiondemo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mufcryan.base.bean.BaseResponse
import com.mufcryan.objectdetectiondemo.bean.DetectionRequest
import com.mufcryan.objectdetectiondemo.bean.DetectionResponse
import com.mufcryan.objectdetectiondemo.repository.DetectionRepository

class DetectionViewModel: ViewModel() {
    private val detectionRepository = DetectionRepository()
    val detectionResponse = MutableLiveData<BaseResponse<DetectionResponse>>()
    fun requestDetection(requestCode: String, filePath: String){
        val request = DetectionRequest()
        request.filePath = filePath
        request.requestCode = requestCode
        detectionRepository.processDataForResponse(request, detectionResponse)
    }
}