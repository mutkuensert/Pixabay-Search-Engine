package com.mutkuensert.pixabaysearchengine.ui.imagesscreen

import android.app.Application
import android.content.Context
import android.util.Log
import com.mutkuensert.pixabaysearchengine.data.ImageHitsModel
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream

private const val TAG = "ImagesRecyclerAdapterClickListenerImpl"
open class ImagesRecyclerAdapterClickListenerImpl(private val application: Application): ImagesRecyclerAdapterClickListener {

    override fun onClick(hitItem: ImageHitsModel) {
        val request = Request.Builder()
            .url("https://cdn.pixabay.com/photo/2014/06/03/19/38/test-361512_960_720.jpg")
            .build()
        val client = OkHttpClient()

        client.newCall(request).execute().use { response ->
            if(response.isSuccessful){

            }else{
                Log.e(TAG, "Response is not seccessful.")
            }
        }
    }

    open fun writeToFile(context: Context, byteStream: InputStream) {

    }
}