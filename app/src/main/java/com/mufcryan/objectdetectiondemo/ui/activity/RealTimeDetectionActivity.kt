package com.mufcryan.objectdetectiondemo.ui.activity

import android.graphics.*
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.mufcryan.objectdetectiondemo.R
import com.mufcryan.objectdetectiondemo.base.BaseActivity
import com.mufcryan.objectdetectiondemo.ui.view.FrameView
import com.mufcryan.objectdetectiondemo.util.FileUtil
import com.mufcryan.objectdetectiondemo.util.LogUtil
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.ByteArrayOutputStream


class RealTimeDetectionActivity : BaseActivity() {
    companion object {
        private const val MODEL_NAME = "res.pt"
        private val CLASSES = intArrayOf(0, 1, 2, 3, 4, 5)
    }

    private lateinit var svPreview: SurfaceView
    private lateinit var tvResult: TextView
    private lateinit var frameView: FrameView
    private lateinit var btnDetect: View
    private lateinit var surfaceHolder: SurfaceHolder
    private var camera: Camera? = null
    private var cameraId = 0
    private var isDetect = false
    private var module: Module? = null

    override fun getLayoutResId() = R.layout.activity_real_time_detection

    override fun initView() {
        svPreview = findViewById(R.id.sv_preview)
        tvResult = findViewById(R.id.tv_preview)
        frameView = findViewById(R.id.fv_view)
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
                    if(isCameraRelease){
                        return@let
                    }
                    val parameters = it.parameters
                    parameters.previewFormat = ImageFormat.NV21
                    parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
                    it.parameters = parameters
                    if(!isStartPreview){
                        isStartPreview = true
                        it.startPreview()
                    }
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
        }
    }

    override fun initData() {
        try {
            module = Module.load(FileUtil.assetFilePath(this, MODEL_NAME))
        } catch (e: Exception){
            LogUtil.d("zfc", e.message)
        }
    }

    /*override fun onResume() {
        super.onResume()
        svPreview.postDelayed({
            openCamera()
        }, 100)
    }

    override fun onPause() {
        super.onPause()
        stopAndRelease()
    }*/

    override fun isFullScreen(): Boolean {
        return true
    }

    private var isStartPreview = false
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
                    if(!isDetect || isCameraRelease){
                        return@setPreviewCallback
                    }
                    try {
                        val width = 64
                        val height = 64
                        val image = YuvImage(data, ImageFormat.NV21, width, height, null)
                        val stream = ByteArrayOutputStream()
                        image.compressToJpeg(Rect(0, 0, width, height), 80, stream)
                        var bmp = BitmapFactory.decodeByteArray(
                            stream.toByteArray(),
                            0,
                            stream.size()
                        )
                        stream.close()

                        //bmp = rotateMyBitmap(bmp, width, height)
                        recognizeNumber(bmp)
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

    private fun rotateMyBitmap(bmp: Bitmap, width: Int, height: Int): Bitmap {
        //*****旋转一下
        val matrix = Matrix()
        matrix.postRotate(90f)
        val nbmp2 = Bitmap.createBitmap(bmp, 0, 0, height, width, matrix, true)
        return nbmp2
    }

    private var lastResult = -1
    private fun recognizeNumber(bmp: Bitmap){
        module?.let {
            val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                bmp,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB
            )
            val outputTensor = it.forward(IValue.from(inputTensor)).toTensor()
            val scores = outputTensor.dataAsFloatArray
            var maxScore = -Float.MAX_VALUE
            var maxScoreIndex = -1
            scores.forEachIndexed { index, score ->
                if(score > maxScore){
                    maxScore = score
                    maxScoreIndex = index
                }
            }
            val classResult = CLASSES[maxScoreIndex]
            if(lastResult != classResult){
                lastResult = classResult
                tvResult.text = "识别结果：$classResult"
            }
        }
    }
}