package com.mufcryan.objectdetectiondemo.base

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import com.mufcryan.objectdetectiondemo.util.FileUtil
import com.mufcryan.objectdetectiondemo.util.LogUtil
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.InputStream

abstract class BaseActivity: FragmentActivity() {
    companion object {
        private const val MODEL_NAME = "res.pt"
        val CLASSES = intArrayOf(0, 1, 2, 3, 4, 5)
    }

    protected abstract fun getLayoutResId(): Int
    protected open fun initView(){}
    protected open fun initListener(){}
    protected open fun initData(){}
    protected open fun isFullScreen() = false

    private var module: Module? = null
    protected var lastResult = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(isFullScreen()){
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(getLayoutResId())
        loadModule()
        initView()
        initListener()
        initData()
    }

    private fun loadModule(){
        try {
            module = Module.load(FileUtil.assetFilePath(this, MODEL_NAME))
        } catch (e: Exception){
            LogUtil.d("zfc", e.message)
        }
    }

    protected fun getByteArray(filePath: String): ByteArray{
        val inputStream = FileInputStream(filePath)
        return readStream(inputStream)
    }

    @Throws(java.lang.Exception::class)
    protected fun readStream(inStream: InputStream): ByteArray {
        val buffer = ByteArray(1024)
        var len: Int
        val outStream = ByteArrayOutputStream()
        while (inStream.read(buffer).also { len = it } != -1) {
            outStream.write(buffer, 0, len)
        }
        val data = outStream.toByteArray()
        outStream.close()
        inStream.close()
        return data
    }

    protected fun getSizedBitmap(data: ByteArray, width: Int = 64, height: Int = 64): Bitmap{
        val image = YuvImage(data, ImageFormat.NV21, width, height, null)
        val stream = ByteArrayOutputStream()
        image.compressToJpeg(Rect(0, 0, width, height), 80, stream)
        val bmp = BitmapFactory.decodeByteArray(
            stream.toByteArray(),
            0,
            stream.size()
        )
        stream.close()
        return bmp
    }

    protected fun recognizeNumber(bmp: Bitmap): Int{
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
            return CLASSES[maxScoreIndex]
        }
        return -1
    }

    protected fun openActivity(clazz: Class<Activity>){
        val intent = Intent(this, clazz)
        startActivity(intent)
    }
}