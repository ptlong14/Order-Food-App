package com.longpt.projectll1.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ShareScreenshot {
    fun captureAndShare(activity: Activity, view: View){
        val bitmap= getBitmapFromView(view)
        val imgFile= saveBitmapToCache(activity, bitmap)
        if(imgFile!=null){
            val authority= "${activity.packageName}.fileprovider"
            val contentUri: Uri
            try{
                contentUri= FileProvider.getUriForFile(activity, authority, imgFile)
            }catch (e: IllegalArgumentException){
                Toast.makeText(activity, "Không thể chia sẻ ảnh. Vui lòng thử lại.", Toast.LENGTH_SHORT).show()
                return
            }
            val shareIntent= Intent(Intent.ACTION_SEND)
            shareIntent.type="image/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            activity.startActivity(Intent.createChooser(shareIntent, "Chia sẻ món ăn"))
        }
    }

    private fun saveBitmapToCache (activity: Activity, bitmap: Bitmap) :File? {
        val cachePath = File(activity.cacheDir, "images").apply {
            if(!exists()) mkdirs()
        }

        val fileName= "img_${System.currentTimeMillis()}.png"
        val imgFile= File(cachePath, fileName)
        return try{
            FileOutputStream(imgFile).use { out->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
            }
            imgFile
        }catch (e: IOException){
            Toast.makeText(activity, "Lưu ảnh thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val width = if(view.width >0) view.width else view.measuredWidth
        val height= if(view.height>0) view.height else view.measuredHeight
        val bitmap: Bitmap = createBitmap(width.coerceAtLeast(1), height.coerceAtLeast(1))
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}