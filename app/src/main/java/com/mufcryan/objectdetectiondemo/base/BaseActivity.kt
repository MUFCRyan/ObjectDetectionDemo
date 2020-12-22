package com.mufcryan.objectdetectiondemo.base

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.mufcryan.objectdetectiondemo.util.FileUtil
import com.mufcryan.objectdetectiondemo.util.LogUtil
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*


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
    private var tts: TextToSpeech? = null
    protected var isSupportChinese = false

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
            tts = TextToSpeech(applicationContext) { status ->
                if(status == TextToSpeech.SUCCESS){
                    tts?.let {
                        val result = it.setLanguage(Locale.CHINESE)
                        if(result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA){
                            it.language = Locale.US
                            isSupportChinese = false
                        } else {
                            isSupportChinese = true
                        }
                        it.setPitch(1f)
                    }
                } else {
                    Toast.makeText(this, "缺少 TTS 引擎，请下载", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception){
            LogUtil.d("zfc", e.message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.let {
            it.stop()
            it.shutdown()
            tts = null
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

    protected fun getSizedBitmap(data: ByteArray, width: Int = 64, height: Int = 64): Bitmap?{
        return nv21ToBitmap(data, width, height)
    }

    protected fun nv21ToBitmap(nv21: ByteArray, width: Int, height: Int): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val image = YuvImage(nv21, ImageFormat.NV21, width, height, null)
            val stream = ByteArrayOutputStream()
            image.compressToJpeg(Rect(0, 0, width, height), 100, stream)
            //将rawImage转换成bitmap
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.RGB_565
            bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size(), options)
            bitmap = Bitmap.createScaledBitmap(bitmap, 64, 64,true)
            bitmap = rotateMyBitmap(bitmap)
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun rotateMyBitmap(bmp: Bitmap): Bitmap {
        //*****旋转一下
        val m = Matrix()
        m.postRotate(90f)
        return Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, m, true)
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

    protected fun speakNumber(number: Int){
        tts?.let {
            val text = if(isSupportChinese){
                "识别结果为$number"
            } else {
                "The result of detect is $number"
            }
            it.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    protected fun openActivity(clazz: Class<Activity>){
        val intent = Intent(this, clazz)
        startActivity(intent)
    }
}