package com.mutkuensert.pixabaysearchengine.ui.imagesscreen

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.mutkuensert.pixabaysearchengine.data.ImageHitsModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

private const val TAG = "ImagesRecyclerAdapterClickListenerImpl"
class ImagesRecyclerAdapterClickListenerImplTestVersion: ImagesRecyclerAdapterClickListenerImpl(ApplicationProvider.getApplicationContext()) {
    lateinit var responseWillBeCheckedInTest: Response
    override fun downloadUrlOnClick(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()
        val client = OkHttpClient()

        client.newCall(request).execute().use {
            responseWillBeCheckedInTest = it
            if(it.isSuccessful){
                writeToFile(ApplicationProvider.getApplicationContext(), it.body!!.byteStream())
            }else{
                Log.e(TAG, "The response is not successful.")
            }
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