package com.mutkuensert.pixabaysearchengine.ui.imagesscreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.*

private const val TAG = "ImagesRecyclerAdapterClickListenerImpl"
open class ImagesRecyclerAdapterClickListenerImpl: ImagesRecyclerAdapterClickListener {

    override var startForResult: ActivityResultLauncher<Intent>? = null
    @Volatile override var data: ByteArray? = null
    override var scope: CoroutineScope? = CoroutineScope(Job() + Dispatchers.IO)

    override fun downloadUrlOnClick(url: String) {
        scope?.launch {
            val request = Request.Builder()
                .url(url)
                .build()
            val client = OkHttpClient()

            client.newCall(request).execute().use {
                if(it.isSuccessful){
                    data = it.body!!.bytes()
                    createEmptyFile()
                }else{
                    Log.e(TAG, "Response is not successful.")
                }
            }
        }

    }

    private fun createEmptyFile(){
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }

        startForResult?.launch(intent)
    }

    override fun writeToFile(context: Context, uri: Uri?) {
        var bufferedInputStream: BufferedInputStream? = null
        var bufferedOutputStream: BufferedOutputStream? = null
        try {
            context.contentResolver.openFileDescriptor(uri!!,"wt")?.use {
                bufferedInputStream = BufferedInputStream(data!!.inputStream())
                bufferedOutputStream = BufferedOutputStream(FileOutputStream(it.fileDescriptor))

                var read: Int
                while(bufferedInputStream!!.read().also { read = it } > -1 ){
                    bufferedOutputStream!!.write(read)
                }
                bufferedOutputStream?.flush()
            }
        } catch(error: IOException) {
            Log.e(TAG,"${error.printStackTrace()}")
        } catch(error: FileNotFoundException) {
            Log.e(TAG,"${error.printStackTrace()}")
        }catch(error: NullPointerException) {
            Log.e(TAG, "${error.printStackTrace()}")
        } finally {
            bufferedInputStream?.close()
            bufferedOutputStream?.close()
        }
    }
}