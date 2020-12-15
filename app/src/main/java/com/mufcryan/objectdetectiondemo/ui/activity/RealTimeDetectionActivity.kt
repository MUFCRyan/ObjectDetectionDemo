package com.mufcryan.objectdetectiondemo.ui.activity

import android.graphics.*
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.mufcryan.objectdetectiondemo.R
import com.mufcryan.objectdetectiondemo.base.BaseActivity
import com.mufcryan.objectdetectiondemo.util.LogUtil
import com.mufcryan.objectdetectiondemo.util.PhotoUtil
import java.io.ByteArrayOutputStream


class RealTimeDetectionActivity : BaseActivity() {
    private lateinit var svPreview: SurfaceView
    private lateinit var ivPreview: ImageView
    private lateinit var btnDetect: View
    private lateinit var surfaceHolder: SurfaceHolder
    private var camera: Camera? = null
    private var cameraId = 0

    override fun getLayoutResId() = R.layout.activity_real_time_detection

    override fun initView() {
        svPreview = findViewById(R.id.sv_preview)
        ivPreview = findViewById(R.id.iv_preview)
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
                    parameters.previewFormat = ImageFormat.NV21
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
                it.setPreviewCallback { data, camera ->
                    val size = camera.parameters.previewSize
                    try {
                        val width = ivPreview.layoutParams.width
                        val height = ivPreview.layoutParams.height
                        val image = YuvImage(data, ImageFormat.NV21, size.width, size.height, null)
                        val stream = ByteArrayOutputStream()
                        image.compressToJpeg(Rect(0, 0, size.width, size.height), 80, stream)
                        var bmp = BitmapFactory.decodeByteArray(
                            stream.toByteArray(),
                            0,
                            stream.size()
                        )
                        stream.close()
                        bmp = rotateMyBitmap(bmp, width, height)
                        Glide.with(ivPreview)
                            .load(bmp)
                            .into(ivPreview)
                    } catch (ex: java.lang.Exception) {
                        LogUtil.e("Sys", "Error:" + ex.message)
                    }


                }
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

    private fun rotateMyBitmap(bmp: Bitmap, width: Int, height: Int): Bitmap {
        //*****旋转一下
        val matrix = Matrix()
        matrix.postRotate(90f)
        val bitmap =
            Bitmap.createBitmap(height, width, Bitmap.Config.RGB_565)
        val nbmp2 = Bitmap.createBitmap(bmp, 0, 0, height, width, matrix, true)
        return nbmp2
    }

}