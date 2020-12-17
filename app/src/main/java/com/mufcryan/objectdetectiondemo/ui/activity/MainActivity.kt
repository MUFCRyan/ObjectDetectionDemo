package com.mufcryan.objectdetectiondemo.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.mufcryan.objectdetectiondemo.*
import com.mufcryan.objectdetectiondemo.base.BaseActivity
import com.mufcryan.objectdetectiondemo.ui.view.FrameView
import com.mufcryan.objectdetectiondemo.util.PhotoUtil
import com.mufcryan.objectdetectiondemo.viewmodel.DetectionViewModel
import fr.castorflex.android.circularprogressbar.CircularProgressBar
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@RuntimePermissions
class MainActivity : BaseActivity() {
    private lateinit var ivPreview: ImageView
    private lateinit var btnTake: View
    private lateinit var btnSelect: View
    private lateinit var btnRetry: View
    private lateinit var progress: CircularProgressBar
    private lateinit var flProgress: View
    private lateinit var viewModel: DetectionViewModel
    private var filePath = ""
    private val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath + "/Camera")
    private lateinit var tempFile: File

    override fun getLayoutResId() = R.layout.activity_main

    override fun initView() {
        viewModel = ViewModelProviders.of(this).get(DetectionViewModel::class.java)
        ivPreview = findViewById(R.id.iv_preview)
        btnTake = findViewById(R.id.btn_take)
        btnSelect = findViewById(R.id.btn_select)
        btnRetry = findViewById(R.id.btn_retry)
        flProgress = findViewById(R.id.fl_progress)
        progress = findViewById(R.id.progress_bar)
    }

    override fun initListener() {
        btnTake.setOnClickListener {
            if(flProgress.visibility == View.VISIBLE){
                return@setOnClickListener
            }
            createTempFileWithPermissionCheck()
            takePhotoWithPermissionCheck()
        }

        btnSelect.setOnClickListener {
            if(flProgress.visibility == View.VISIBLE){
                return@setOnClickListener
            }
            selectImageWithPermissionCheck()
        }

        btnRetry.setOnClickListener {
            if(flProgress.visibility == View.VISIBLE){
                return@setOnClickListener
            }
            requestDetect(filePath)
        }

        viewModel.detectionResponse.observe(this, Observer {
            flProgress.visibility = View.GONE
            if(it.isSuccessful){
                loadPic(it.data.image)
            }
        })
    }

    override fun isFullScreen(): Boolean {
        return true
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun createTempFile(){
        if(!dir.exists()){
            dir.mkdirs()
        }
        val millis = System.currentTimeMillis()
        val imgName: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(millis)
        tempFile = File(dir, "Detection_$imgName.jpg")
        tempFile.createNewFile()
    }

    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET)
    fun takePhoto() {
        // 打开系统拍照程
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", tempFile)
        camera.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(camera,
            REQUEST_CODE_CAMERA
        )
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET)
    fun selectImage() {
        // 打开系统图库选择图片
        val picture = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(picture,
            REQUEST_CODE_PICTURE
        )
    }

    private fun requestDetect(filePath: String) {
        flProgress.visibility = View.VISIBLE
        Toast.makeText(this, "正在识别中，请稍后。。。", Toast.LENGTH_LONG).show()
        viewModel.requestDetection(filePath, filePath)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            //读取返回码
            when (requestCode) {
                REQUEST_CODE_PICTURE -> {
                    Log.e("zfc","相册")
                    val uri: Uri = data!!.data
                    try {
                        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                        filePath = PhotoUtil.getRealPathFromURI(uri)
                        loadPic(filePath)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                REQUEST_CODE_CAMERA -> {
                    Log.e("zfc","相机")
                    try {
                        scanImageFile(
                            this,
                            tempFile
                        )
                        PhotoUtil.adjustRotation(tempFile.absolutePath)
                        filePath = tempFile.absolutePath
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            loadPic(filePath)
            requestDetect(filePath)
        }
    }

    private fun loadPic(filePath: String){
        Glide.with(ivPreview)
            .load(filePath)
            .into(ivPreview)
    }

    companion object {
        private const val REQUEST_CODE_CAMERA = 1
        private const val REQUEST_CODE_PICTURE = 2

        /**
         * 图片保存后，通知系统扫描
         */
        fun scanImageFile(context: Context, file: File) {
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
            MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), arrayOf("image/jpeg")) { path: String?, uri: Uri? -> }
        }
    }
}