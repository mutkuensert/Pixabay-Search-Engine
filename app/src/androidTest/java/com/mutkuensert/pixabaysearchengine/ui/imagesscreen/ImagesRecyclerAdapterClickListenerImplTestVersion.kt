package com.mutkuensert.pixabaysearchengine.ui.imagesscreen

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

private const val TAG = "ImagesRecyclerAdapterClickListenerImpl"
class ImagesRecyclerAdapterClickListenerImplTestVersion: ImagesRecyclerAdapterClickListenerImpl(ApplicationProvider.getApplicationContext()) {
    fun onClick(): Boolean {
        val request = Request.Builder()
            .url("https://raw.githubusercontent.com/mutkuensert/Files/main/test_image.jpg")
            .build()
        val client = OkHttpClient()

        client.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
    }

    override fun writeToFile(context: Context, byteStream: InputStream) {
        var bufferedInputStream: BufferedInputStream? = null
        var bufferedOutputStream: BufferedOutputStream? = null

        try {
            val file = File.createTempFile("pic", ".jpg")
            bufferedInputStream = BufferedInputStream(byteStream)
            bufferedOutputStream = BufferedOutputStream(FileOutputStream(file))
            var read: Int
            while(bufferedInputStream.read().also { read = it }>-1){
                bufferedOutputStream.write(read)
            }
            bufferedOutputStream.flush()
        } finally {
            bufferedInputStream?.close()
            bufferedOutputStream?.close()
        }
    }
}