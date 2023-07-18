package com.mutkuensert.pixabaysearchengine.libraries.downloader

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mutkuensert.pixabaysearchengine.R
import com.mutkuensert.pixabaysearchengine.libraries.appscope.AppScope
import kotlin.random.Random
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

private const val TAG = "Downloader"

/**
 * @property initActivityResultLauncher Initialize activityResultLauncher
 * in a Fragment or Activity.
 *
 * @property setFileFormatExtractor Set if the file format isn't the part after the last dot in the url.
 *
 * @property setFileFormat Set the file format if it is certain.
 */
class Downloader(private val scope: AppScope) {
    private var fileFormat: String? = null
    private var fileFormatExtractor: (url: String) -> String = { it.substringAfterLast(".") }
    private var startForResult: ActivityResultLauncher<Intent>? = null
    private var notificationId: Int = Random(System.nanoTime()).nextInt()
    private var response: Response? = null

    fun downloadUrl(url: String) {
        Log.i(TAG, "$url is going to be downloaded.")

        scope.launch {
            val request = Request.Builder()
                .url(url)
                .build()
            val client = OkHttpClient()

            response = client.newCall(request).execute()
            if (response!!.isSuccessful) {
                val format = fileFormat ?: fileFormatExtractor.invoke(url)
                createEmptyFileIntentAndStartLauncher(format)
            } else {
                Log.e(TAG, "Response is not successful.")
            }
        }
    }

    private fun createEmptyFileIntentAndStartLauncher(format: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)

            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(format)

            putExtra(Intent.EXTRA_TITLE, "file")
        }

        startForResult?.launch(intent)
    }

    @SuppressLint("MissingPermission")
    fun writeToFile(context: Context, uri: Uri?, channelId: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            uri
        ).apply { addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        scope.launch {
            val builder = NotificationCompat.Builder(context, channelId).apply {
                setContentTitle("Download")
                setContentText("Download in progress")
                setSmallIcon(R.drawable.ic_search)
                priority = NotificationCompat.PRIORITY_LOW
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

                    context.contentResolver.openFileDescriptor(uri!!, "wt")
                        ?.use { parcelFileDescriptor ->
                            outputStream = FileOutputStream(parcelFileDescriptor.fileDescriptor)

                            val buff = ByteArray(1024)
                            var read: Int
                            var bytesCopied: Long = 0
                            val stream = response!!.body!!.byteStream()
                            val contentLength = response!!.body!!.contentLength()
                            var previousTimeMillis = System.currentTimeMillis()

                            while (stream.read(buff, 0, buff.size).also { read = it } > -1) {
                                outputStream!!.write(buff, 0, read)
                                bytesCopied += read
                                val progressCurrent = ((bytesCopied * 100) / contentLength).toInt()

                                if ((System.currentTimeMillis() - previousTimeMillis) > 1000) {
                                    builder.setProgress(PROGRESS_MAX, progressCurrent, false)
                                    notify(notificationId, builder.build())
                                    previousTimeMillis = System.currentTimeMillis()
                                }
                            }
                        }

                    builder.setContentText("Download complete")
                        .setProgress(0, 0, false)
                    notify(notificationId, builder.build())
                    notificationId = Random(System.nanoTime()).nextInt()
                }

            } catch (error: IOException) {
                Log.e(TAG, "${error.printStackTrace()}")

            } catch (error: FileNotFoundException) {
                Log.e(TAG, "${error.printStackTrace()}")

            } catch (error: NullPointerException) {
                Log.e(TAG, "${error.printStackTrace()}")

            } finally {
                response!!.body?.close()
                outputStream?.close()
            }
        }
    }

    fun initActivityResultLauncher(activityResultLauncher: Downloader.() -> ActivityResultLauncher<Intent>?) {
        startForResult = activityResultLauncher.invoke(this)
    }

    fun setFileFormat(type: String) {
        fileFormat = type
    }

    /**
     * Default is url.substringAfterLast(".")
     */
    fun setFileFormatExtractor(extractor: (url: String) -> String) {
        fileFormatExtractor = extractor
    }
}