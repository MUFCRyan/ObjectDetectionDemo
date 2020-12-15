package com.mufcryan.objectdetectiondemo.ui.activity

import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import com.mufcryan.objectdetectiondemo.R
import com.mufcryan.objectdetectiondemo.base.BaseActivity
import permissions.dispatcher.RuntimePermissions

class RealTimeDetectionActivity : BaseActivity() {
    private lateinit var svPreview: SurfaceView
    private lateinit var btnDetect: View
    private lateinit var surfaceHolder: SurfaceHolder
    private var camera: Camera? = null
    private var cameraId = 1

    override fun getLayoutResId() = R.layout.activity_real_time_detection

    override fun initView() {
        svPreview = findViewById(R.id.sv_preview)
        btnDetect = findViewById(R.id.btn_start_detect)
    }

    override fun initListener() {
        surfaceHolder = svPreview.holder
        surfaceHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {
                camera?.let {
                    val parameters = it.parameters
                    parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
                    it.parameters = parameters
                    it.startPreview()
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                stopAndRelease()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                openCamera()
            }
        })

        btnDetect.setOnClickListener {

        }
    }

    override fun initData() {

    }

    override fun isFullScreen(): Boolean {
        return true
    }

    private fun openCamera() {
        try {
            // 打开摄像机
            camera = Camera.open(cameraId)
            camera?.let {
                it.setDisplayOrientation(90)
                // 绑定 Surface 并开启预览
                it.setPreviewDisplay(surfaceHolder)
                it.startPreview()
            }
        } catch (e: Exception) {
            releaseCamera()
            Toast.makeText(this@RealTimeDetectionActivity, "Surface 创建失败，请重试", Toast.LENGTH_SHORT)
                .show();
        }
    }

    private fun stopAndRelease(){
        camera?.let {
            it.stopPreview()
            releaseCamera()
        }
    }

    private fun releaseCamera(){
        camera?.let {
            it.release()
            camera = null
        }
    }

    // 翻转摄像机
    private fun switchCamera() {
        cameraId = if (cameraId == 1) 0 else 1
        stopAndRelease()
        openCamera()
    }
}