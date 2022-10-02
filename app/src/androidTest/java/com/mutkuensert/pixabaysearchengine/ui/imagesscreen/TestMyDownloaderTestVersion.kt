package com.mutkuensert.pixabaysearchengine.ui.imagesscreen


import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class TestMyDownloaderTestVersion {

    @get:Rule
    var rule = HiltAndroidRule(this)

    private lateinit var downloader: MyDownloaderTestVersion

    @Before
    fun initializeListenerImpl(){
        downloader = MyDownloaderTestVersion()
    }

    @Test
    fun isDataResponseSuccessful(){
        downloader.downloadUrl("https://raw.githubusercontent.com/mutkuensert/Files/main/test_image.jpg")
        assert(downloader.response!!.isSuccessful)
    }
}