package com.mufcryan.objectdetectiondemo.ui.activity

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mufcryan.objectdetectiondemo.R
import com.mufcryan.objectdetectiondemo.base.BaseActivity
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class SplashActivity : BaseActivity() {
    private lateinit var btnPhotoDetect: View
    private lateinit var btnVideoDetect: View

    override fun getLayoutResId() = R.layout.activity_splash

    override fun initView() {
        btnPhotoDetect = findViewById(R.id.btn_photo_detect)
        btnVideoDetect = findViewById(R.id.btn_video_detect)
    }

    override fun initListener() {
        btnPhotoDetect.setOnClickListener {
            openActivity(MainActivity().javaClass)
        }

        btnVideoDetect.setOnClickListener {
            openVideoDetectWithPermissionCheck()
        }
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun openVideoDetect(){
        openActivity(RealTimeDetectionActivity().javaClass)
    }
}