package com.mufcryan.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import java.io.FileOutputStream
import java.io.IOException


object PhotoUtil {
    fun adjustRotation(imgPath: String){
        val degree = readPictureDegree(imgPath)
        if(degree%360 != 0){
            val bitmap = rotateBitmapByDegree(BitmapFactory.decodeFile(imgPath), degree)
            savePhotoToSD(bitmap, imgPath)
        }
    }

    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    private fun readPictureDegree(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }

    private fun rotateBitmapByDegree(bm: Bitmap, degree: Int): Bitmap {
        Log.d("zfc", "rotateBitmapByDegree")
        var returnBm: Bitmap? = null

        // 根据旋转角度，生成旋转矩阵
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(
                bm, 0, 0, bm.width,
                bm.height, matrix, true
            )
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        }
        if (returnBm == null) {
            returnBm = bm
        }
        if (bm != returnBm) {
            bm.recycle()
        }
        return returnBm
    }

    /**
     * 保存Bitmap图片在SD卡中
     * 如果没有SD卡则存在手机中
     *
     * @param bitmap       需要保存的Bitmap图片
     * @param originPath    文件的原路径
     * @param isReplaceFile 是否替换原文件
     * @return 保存成功时返回图片的路径，失败时返回null
     */
    private fun savePhotoToSD(bitmap: Bitmap, originPath: String){
        var outStream: FileOutputStream? = null
        if (TextUtils.isEmpty(originPath)) return
        try {
            outStream = FileOutputStream(originPath)
            // 把数据写入文件，100表示不压缩
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                outStream?.close()
                bitmap.recycle()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getRealPathFromURI(context: Context, contentURI: Uri): String {
        val result: String
        val cursor = context.contentResolver.query(contentURI, null, null, null, null)
        //不能直接调用contentprovider的接口函数，需要使用contentresolver对象，通过URI间接调用contentprovider
        if (cursor == null) {
            // Source is Dropbox or other similar local file path
            result = contentURI.path ?: ""
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }
}