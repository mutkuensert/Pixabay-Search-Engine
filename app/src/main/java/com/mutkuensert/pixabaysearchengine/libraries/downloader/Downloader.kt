package com.mutkuensert.pixabaysearchengine.libraries.downloader

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mutkuensert.pixabaysearchengine.R
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.FileOutputStream
import java.io.OutputStream

private const val TAG = "Downloader"
private const val DOWNLOADER_NOTIF_CHANNEL_ID: String = "notification_channel_1"

/**
 * @property initActivityResultLauncher Initialize activityResultLauncher
 * in a Fragment or Activity.
 *
 * @property setFileFormatExtractor Set if the file format isn't the part after the last dot in the url.
 *
 * @property setFileNameExtractor Set if the name of the file is not between the last slash and the first dot after the last slash.
 *
 * @property setFileFormat Set the file format if it is certain.
 *
 * @property notificationBuilder can be edited.
 */
open class Downloader(private val scope: CoroutineScope, private val context: Context) {
    private var fileFormat: String? = null
    private var fileFormatExtractor: (url: String) -> String = { it.substringAfterLast(".") }
    private var fileNameExtractor: (url: String) -> String =
        { it.substringAfterLast("/").substringBefore(".") }
    private var startForResult: ActivityResultLauncher<Intent>? = null
    private var notificationId: Int = Random(System.nanoTime()).nextInt()
    private var response: Response? = null
    private var currentFileName = "file"

    val notificationCompat = NotificationManagerCompat.from(context)
    val notificationBuilder =
        NotificationCompat.Builder(context, DOWNLOADER_NOTIF_CHANNEL_ID).apply {
            setContentTitle("Download")
            setSmallIcon(R.drawable.ic_search)
            priority = NotificationCompat.PRIORITY_LOW
            setAutoCancel(true)
        }

    companion object {
        private const val PROGRESS_MAX = 100
    }

    init {
        createNotificationChannel()
    }

    private fun createEmptyFileIntentAndStartLauncher(format: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)

            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(format)

            putExtra(Intent.EXTRA_TITLE, currentFileName)
        }

        startForResult?.launch(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance =
                NotificationManager.IMPORTANCE_LOW // https://stackoverflow.com/a/45920861
            val channel = NotificationChannel(DOWNLOADER_NOTIF_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

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
                currentFileName = fileNameExtractor.invoke(url)
                createEmptyFileIntentAndStartLauncher(format)
            } else {
                Log.e(TAG, "Response is not successful.")
            }
        }
    }

    fun initActivityResultLauncher(activityResultLauncher: Downloader.() -> ActivityResultLauncher<Intent>?) {
        startForResult = activityResultLauncher.invoke(this)
    }

    private fun notifyIfPermissionIsGranted(
        id: Int,
        notification: Notification
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationCompat.notify(id, notification)
        }
    }

    open fun onDownloadComplete() {
        notificationBuilder.setContentText("Downloaded File: $currentFileName")
            .setProgress(0, 0, false)

        notifyIfPermissionIsGranted(notificationId, notificationBuilder.build())
    }

    open fun onDownloadStart() {
        notificationBuilder.setProgress(PROGRESS_MAX, 0, false)
        notificationBuilder.setContentText("Downloading $currentFileName")
        notifyIfPermissionIsGranted(notificationId, notificationBuilder.build())
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

    /**
     * Default is url.substringAfterLast("/").substringBeforeLast(".")
     */
    fun setFileNameExtractor(extractor: (url: String) -> String) {
        fileNameExtractor = extractor
    }

    private fun setNotificationBuilderIntent(uri: Uri) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            uri
        ).apply { addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        notificationBuilder.setContentIntent(pendingIntent)
    }

    fun writeToFile(uri: Uri) {
        setNotificationBuilderIntent(uri)

        scope.launch {
            var outputStream: OutputStream? = null

            try {
                notificationId = Random(System.nanoTime()).nextInt()
                onDownloadStart()

                context.contentResolver.openFileDescriptor(uri, "wt")
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
                            val progressCurrent =
                                ((bytesCopied * PROGRESS_MAX) / contentLength).toInt()

                            if ((System.currentTimeMillis() - previousTimeMillis) > 1000) {
                                notificationBuilder.setProgress(
                                    PROGRESS_MAX,
                                    progressCurrent,
                                    false
                                )
                                notifyIfPermissionIsGranted(
                                    notificationId,
                                    notificationBuilder.build()
                                )
                                previousTimeMillis = System.currentTimeMillis()
                            }
                        }
                    }

                onDownloadComplete()

            } catch (error: Throwable) {
                Log.e(TAG, error.stackTraceToString())
            } finally {
                response!!.body?.close()
                outputStream?.close()
            }
        }
    }
}