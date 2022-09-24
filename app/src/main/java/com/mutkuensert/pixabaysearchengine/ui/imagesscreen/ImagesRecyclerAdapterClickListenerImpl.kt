package com.mutkuensert.pixabaysearchengine.ui.imagesscreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mutkuensert.pixabaysearchengine.R
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
    override var notificationId: Int = 0

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

    override fun writeToFile(context: Context, uri: Uri?, channelId: String) {
        val builder = NotificationCompat.Builder(context, channelId).apply {
            setContentTitle("Picture Download")
            setContentText("Download in progress")
            setSmallIcon(R.drawable.ic_search)
            setPriority(NotificationCompat.PRIORITY_LOW)
        }
        var outputStream: OutputStream? = null

        val PROGRESS_MAX = 100
        val PROGRESS_CURRENT = 0

        try {
            NotificationManagerCompat.from(context).apply {
                builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false)
                notify(notificationId, builder.build())
                context.contentResolver.openFileDescriptor(uri!!,"wt")?.use {
                    outputStream = FileOutputStream(it.fileDescriptor)

                    val buff = ByteArray(1024)
                    var read: Int
                    var bytesCopied = 0
                    val stream = response.body!!.byteStream()
                    val contentLength = response.body!!.contentLength()
                    var currentTimeMillis = System.currentTimeMillis()

                    while(stream.read(buff, 0, buff.size).also { read = it } > -1 ){
                        outputStream!!.write(buff, 0 , read)
                        bytesCopied += read
                        val progressCurrent = ((bytesCopied*100)/contentLength).toInt()
                        if((System.currentTimeMillis() - currentTimeMillis)>1000){
                            builder.setProgress(PROGRESS_MAX, progressCurrent, false);
                            notify(notificationId, builder.build());
                        }
                        currentTimeMillis = System.currentTimeMillis()
                    }
                }

                builder.setContentText("Download complete")
                    .setProgress(0, 0, false)
                notify(notificationId, builder.build())
                notificationId += 1
            }

        } catch(error: IOException) {
            Log.e(TAG,"${error.printStackTrace()}")

        } catch(error: FileNotFoundException) {
            Log.e(TAG,"${error.printStackTrace()}")

        }catch(error: NullPointerException) {
            Log.e(TAG, "${error.printStackTrace()}")

        } finally {
            response.body?.close()
            outputStream?.close()
        }
    }
}