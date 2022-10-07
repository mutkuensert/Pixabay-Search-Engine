package com.mutkuensert.pixabaysearchengine.ui

import android.app.PendingIntent
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

private const val TAG = "MyDownloader"
class MyDownloader(private val contentType: String) : MyDownloaderInterface {

    override var startForResult: ActivityResultLauncher<Intent>? = null //Init in its fragment.
    @Volatile override var response: Response? = null
    override var scope: CoroutineScope? = CoroutineScope(Job() + Dispatchers.IO)
    override var notificationId: Int = 0

    override fun downloadUrl(url: String) {
        Log.i(TAG,"The Url will be downloaded: $url")
        scope?.launch {
            val request = Request.Builder()
                .url(url)
                .build()
            val client = OkHttpClient()

            response = client.newCall(request).execute()
            if(response!!.isSuccessful){
                withContext(Dispatchers.Main) {
                    val fileType = url.substringAfterLast(".").substringBefore("?")
                    createEmptyFile(fileType)
                }
            }else{
                Log.e(TAG, "Response is not successful.")
            }
        }

    }

    override fun createEmptyFile(fileType: String){
        Log.i(TAG,"File type: $fileType")
        val subtype = if(fileType.equals("jpg")) "jpeg" else fileType //There is no jpg in mime types: https://android.googlesource.com/platform/external/mime-support/+/9817b71a54a2ee8b691c1dfa937c0f9b16b3473c/mime.types

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "$contentType/$subtype"
            putExtra(Intent.EXTRA_TITLE, "file")
        }

        startForResult?.launch(intent)
    }

    override fun writeToFile(context: Context, uri: Uri?, channelId: String) {
        val intent = Intent(Intent.ACTION_VIEW, uri).apply { addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        scope?.launch {
            val builder = NotificationCompat.Builder(context, channelId).apply {
                setContentTitle("Download")
                setContentText("Download in progress")
                setSmallIcon(R.drawable.ic_search)
                setPriority(NotificationCompat.PRIORITY_LOW)
                setContentIntent(pendingIntent)
                setAutoCancel(true)

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
                        var bytesCopied: Long = 0
                        val stream = response!!.body!!.byteStream()
                        val contentLength = response!!.body!!.contentLength()
                        var previousTimeMillis = System.currentTimeMillis()

                        while(stream.read(buff, 0, buff.size).also { read = it } > -1 ){
                            outputStream!!.write(buff, 0 , read)
                            bytesCopied += read
                            val progressCurrent = ((bytesCopied*100)/contentLength).toInt()
                            if((System.currentTimeMillis() - previousTimeMillis)>1000){
                                builder.setProgress(PROGRESS_MAX, progressCurrent, false);
                                notify(notificationId, builder.build());
                                previousTimeMillis = System.currentTimeMillis()
                            }
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
                response!!.body?.close()
                outputStream?.close()
            }
        }
    }
}