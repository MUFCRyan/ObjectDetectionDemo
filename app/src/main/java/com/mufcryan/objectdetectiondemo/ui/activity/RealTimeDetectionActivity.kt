package com.mufcryan.objectdetectiondemo.ui.activity

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.mufcryan.objectdetectiondemo.R
import com.mufcryan.objectdetectiondemo.base.BaseActivity
import com.mufcryan.objectdetectiondemo.ui.view.FrameView
import com.mufcryan.objectdetectiondemo.util.LogUtil


class RealTimeDetectionActivity : BaseActivity() {

    private lateinit var svPreview: SurfaceView
    private lateinit var ivPreview: ImageView
    private lateinit var tvResult: TextView
    private lateinit var frameView: FrameView
    private lateinit var btnDetect: View
    private lateinit var surfaceHolder: SurfaceHolder
    private var camera: Camera? = null
    private var cameraId = 0
    private var isDetect = false

    override fun getLayoutResId() = R.layout.activity_real_time_detection

    override fun initView() {
        svPreview = findViewById(R.id.sv_preview)
        tvResult = findViewById(R.id.tv_preview)
        frameView = findViewById(R.id.fv_view)
        btnDetect = findViewById(R.id.btn_start_detect)
        ivPreview = findViewById(R.id.iv_preview)
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
                    if (isCameraRelease) {
                        return@let
                    }
                    val parameters = it.parameters
                    parameters.previewFormat = ImageFormat.NV21
                    parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
                    it.parameters = parameters
                    it.setPreviewDisplay(holder)
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
            isDetect = true
            Toast.makeText(this, "正在识别中", Toast.LENGTH_LONG).show()
        }
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        camera?.startPreview()
    }

    override fun onPause() {
        super.onPause()
        camera?.stopPreview()
    }

    override fun isFullScreen(): Boolean {
        return true
    }

    private var isDetecting = false
    private fun openCamera() {
        if(camera != null){
            return
        }
        try {
            // 打开摄像机
            camera = Camera.open(cameraId)
            camera?.let {
                it.setDisplayOrientation(90)
                // 绑定 Surface 并开启预览
                it.setPreviewDisplay(surfaceHolder)
                it.setPreviewCallback { data, camera ->
                    if(!isDetect || isCameraRelease || isDetecting){
                        return@setPreviewCallback
                    }
                    isDetecting = true
                    ivPreview.postDelayed({
                        isDetecting = false
                    }, 1000)
                    try {
                        val size = it.parameters.previewSize
                        val bmp = getSizedBitmap(data, size.width, size.height)
                        bmp?.let { bitmap ->
                            /*Glide.with(ivPreview)
                                .load(bitmap)
                                .into(ivPreview)*/
                            val classResult = recognizeNumber(bitmap)
                            if(lastResult != classResult){
                                lastResult = classResult
                                if(tvResult.visibility != View.VISIBLE){
                                    tvResult.visibility = View.VISIBLE
                                }
                                tvResult.text = "识别结果：$classResult"
                            }
                        }
                    } catch (ex: java.lang.Exception) {
                        LogUtil.e("Sys", "Error:" + ex.message)
                    }


                }
                isCameraRelease = false
            }
        } catch (e: Exception) {
            Toast.makeText(this@RealTimeDetectionActivity, "Surface 创建失败，请重试", Toast.LENGTH_SHORT).show();
            releaseCamera()
        }
    }

    private fun stopAndRelease(){
        camera?.let {
            it.stopPreview()
            it.stopFaceDetection()
            it.stopSmoothZoom()
            releaseCamera()
        }
    }

    private var isCameraRelease = true
    private fun releaseCamera(){
        isCameraRelease = true
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