package com.mufcryan.objectdetectiondemo.bean

import com.mufcryan.net.RetrofitClient.BASE_HOST

class DetectionResponse {
    var image = ""
        get() = BASE_HOST + field
}