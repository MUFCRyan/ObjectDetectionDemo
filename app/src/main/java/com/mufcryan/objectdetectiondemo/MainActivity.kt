package com.mufcryan.objectdetectiondemo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.mufcryan.objectdetectiondemo.ui.FrameView
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.io.IOException


@RuntimePermissions
class MainActivity : FragmentActivity() {
    private lateinit var ivPreview: ImageView
    private lateinit var frameView: FrameView
    private lateinit var btnTake: View
    private lateinit var btnSelect: View
    private lateinit var btnRetry: View
    private lateinit var viewModel: DetectionViewModel
    private var filePath = ""
    private var tempFile = File(Environment.getExternalStorageDirectory(), "fileImg.jpg")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initListener()
    }

    private fun initView() {
        viewModel = ViewModelProviders.of(this).get(DetectionViewModel::class.java)
        ivPreview = findViewById(R.id.iv_preview)
        frameView = findViewById(R.id.fv_view)
        btnTake = findViewById(R.id.btn_take)
        btnSelect = findViewById(R.id.btn_select)
        btnRetry = findViewById(R.id.btn_retry)
    }

    private fun initListener() {
        btnTake.setOnClickListener {
            createTempFileWithPermissionCheck()
            takePhotoWithPermissionCheck()
        }

        btnSelect.setOnClickListener {
            selectImageWithPermissionCheck()
        }

        btnRetry.setOnClickListener {
            requestDetect()
        }

        viewModel.detectionResponse.observe(this, Observer {

        })
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun createTempFile(){
        if(!tempFile.exists()){
            tempFile.createNewFile()
        }
    }

    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun takePhoto() {
        // 打开系统拍照程
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile))
        startActivityForResult(camera, REQUEST_CODE_CAMERA)
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun selectImage() {
        // 打开系统图库选择图片
        val picture = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(picture, REQUEST_CODE_PICTURE)
    }

    private fun requestDetect() {
        viewModel.requestDetection(filePath, filePath)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            //读取返回码
            when (requestCode) {
                REQUEST_CODE_PICTURE -> {
                    Log.e("zfc","相册")
                    val uri01: Uri = data!!.data
                    try {
                        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri01))
                        Glide.with(ivPreview)
                            .load(bitmap)
                            .into(ivPreview)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                REQUEST_CODE_CAMERA -> {
                    Log.e("zfc","相机")
                    try {
                        tempFile = File(Environment.getExternalStorageDirectory(), "fileImg.jpg") //相机取图片数据文件
                        val uri02: Uri = Uri.fromFile(tempFile) //图片文件
                        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri02))
                        Glide.with(ivPreview)
                            .load(bitmap)
                            .into(ivPreview)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_CAMERA = 1
        private const val REQUEST_CODE_PICTURE = 2
    }
}