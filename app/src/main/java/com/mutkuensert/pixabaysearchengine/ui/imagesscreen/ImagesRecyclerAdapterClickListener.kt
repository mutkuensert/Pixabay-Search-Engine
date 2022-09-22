package com.mutkuensert.pixabaysearchengine.ui.imagesscreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import kotlinx.coroutines.CoroutineScope
import okhttp3.Response


interface ImagesRecyclerAdapterClickListener {
    var startForResult: ActivityResultLauncher<Intent>?
    var response: Response
    var scope: CoroutineScope?

    fun downloadUrlOnClick(url: String)
    fun writeToFile(context: Context, uri: Uri?)
}