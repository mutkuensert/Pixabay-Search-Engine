package com.mutkuensert.pixabaysearchengine.ui.imagesscreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.*

private const val TAG = "ImagesRecyclerAdapterClickListenerImplTestVersion"
class ImagesRecyclerAdapterClickListenerImplTestVersion: ImagesRecyclerAdapterClickListener {

    override var startForResult: ActivityResultLauncher<Intent>? = null
    override lateinit var response: Response
    override var scope: CoroutineScope? = null
    override var notificationId: Int = 0

    override fun downloadUrlOnClick(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()
        val client = OkHttpClient()

        client.newCall(request).execute().use {
            response = it
            if(response.isSuccessful){
                writeToFile(ApplicationProvider.getApplicationContext(), null, "Test channel id")
            }else{
                Log.e(TAG, "The response is not successful.")
            }
        }
    }

    override fun writeToFile(context: Context, uri: Uri?, channelId: String) {
        var bufferedInputStream: BufferedInputStream? = null
        var bufferedOutputStream: BufferedOutputStream? = null

        try {
            val file = File.createTempFile("pic", ".jpg")
            bufferedInputStream = BufferedInputStream(response.body?.byteStream())
            bufferedOutputStream = BufferedOutputStream(FileOutputStream(file))
            var read: Int
            while(bufferedInputStream.read().also { read = it }>-1){
                bufferedOutputStream.write(read)
            }
            bufferedOutputStream.flush()
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
            bufferedInputStream?.close()
            bufferedOutputStream?.close()
        }
        //return true
    }
}