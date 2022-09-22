package com.mutkuensert.pixabaysearchengine.ui.imagesscreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.*

private const val TAG = "ImagesRecyclerAdapterClickListenerImpl"
open class ImagesRecyclerAdapterClickListenerImpl: ImagesRecyclerAdapterClickListener {

    override var startForResult: ActivityResultLauncher<Intent>? = null
    @Volatile override lateinit var response: Response
    override var scope: CoroutineScope? = CoroutineScope(Job() + Dispatchers.IO)

    override fun downloadUrlOnClick(url: String) {
        scope?.launch {
            val request = Request.Builder()
                .url(url)
                .build()
            val client = OkHttpClient()

            response = client.newCall(request).execute()
            if(response.isSuccessful){
                withContext(Dispatchers.Main) { createEmptyFile(url.substringAfterLast(".")) }
            }else{
                Log.e(TAG, "Response is not successful.")
            }
        }

    }

    private fun createEmptyFile(imageType: String){
        var format = if(imageType.equals("jpg")) "jpeg" else imageType //There is no jpg in mime types: https://android.googlesource.com/platform/external/mime-support/+/9817b71a54a2ee8b691c1dfa937c0f9b16b3473c/mime.types
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/$format"
            putExtra(Intent.EXTRA_TITLE, "image")
        }

        startForResult?.launch(intent)
    }

    override fun writeToFile(context: Context, uri: Uri?) {
        var outputStream: OutputStream? = null
        try {
            context.contentResolver.openFileDescriptor(uri!!,"wt")?.use {
                outputStream = FileOutputStream(it.fileDescriptor)

                val buff = ByteArray(1024)
                var read: Int
                val stream = response.body!!.byteStream()
                while(stream.read(buff, 0, buff.size).also { read = it } > -1 ){
                    outputStream!!.write(buff, 0 , read)
                }
            }
        } catch(error: IOException) {
            Log.e(TAG,"${error.printStackTrace()}")
            //return false

        } catch(error: FileNotFoundException) {
            Log.e(TAG,"${error.printStackTrace()}")
            //return false

        }catch(error: NullPointerException) {
            Log.e(TAG, "${error.printStackTrace()}")
            //return false

        } finally {
            response.body?.close()
            outputStream?.close()
        }
        //return true
    }
}