package com.mufcryan.objectdetectiondemo

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
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.mufcryan.objectdetectiondemo.ui.FrameView
import com.mufcryan.objectdetectiondemo.util.PhotoUtil
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@RuntimePermissions
class MainActivity : FragmentActivity() {
    private lateinit var ivPreview: ImageView
    private lateinit var frameView: FrameView
    private lateinit var btnTake: View
    private lateinit var btnSelect: View
    private lateinit var btnRetry: View
    private lateinit var viewModel: DetectionViewModel
    private var filePath = ""
    private val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath + "/Camera")
    private lateinit var tempFile: File

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
            requestDetect(filePath)
        }

        viewModel.detectionResponse.observe(this, Observer {
            if(it.isSuccessful){
                frameView.setRect(10, 30, 310, 200)
            }
        })
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
        startActivityForResult(camera, REQUEST_CODE_CAMERA)
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET)
    fun selectImage() {
        // 打开系统图库选择图片
        val picture = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(picture, REQUEST_CODE_PICTURE)
    }

    private fun requestDetect(filePath: String) {
        viewModel.requestDetection(filePath, filePath)
        frameView.setRect(10, 30, 310, 200)
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
                        Glide.with(ivPreview)
                            .load(bitmap)
                            .into(ivPreview)
                        filePath = PhotoUtil.getRealPathFromURI(uri)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                REQUEST_CODE_CAMERA -> {
                    Log.e("zfc","相机")
                    try {
                        scanImageFile(this, tempFile)
                        PhotoUtil.adjustRotation(tempFile.absolutePath)
                        val uri: Uri = Uri.fromFile(tempFile) //图片文件
                        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                        Glide.with(ivPreview)
                            .load(bitmap)
                            .into(ivPreview)
                        filePath = tempFile.absolutePath
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            requestDetect(filePath)
        }
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