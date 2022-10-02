package com.mutkuensert.pixabaysearchengine.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import kotlinx.coroutines.CoroutineScope
import okhttp3.Response

interface MyDownloaderInterface {
    var startForResult: ActivityResultLauncher<Intent>?
    var response: Response?
    var scope: CoroutineScope?
    var notificationId: Int
    fun downloadUrl(url: String)
    fun createEmptyFile(imageType: String)
    fun writeToFile(context: Context, uri: Uri?, channelId: String)
}