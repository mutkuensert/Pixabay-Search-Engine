package com.mutkuensert.pixabaysearchengine.ui.imagesscreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import kotlinx.coroutines.CoroutineScope
import java.io.InputStream


interface ImagesRecyclerAdapterClickListener {
    var startForResult: ActivityResultLauncher<Intent>?
    var data: ByteArray?
    var scope: CoroutineScope?

    fun downloadUrlOnClick(url: String)
    fun writeToFile(context: Context, uri: Uri?)
}