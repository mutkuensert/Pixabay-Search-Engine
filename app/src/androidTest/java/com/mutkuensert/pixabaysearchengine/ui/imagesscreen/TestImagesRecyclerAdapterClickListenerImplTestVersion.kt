package com.mutkuensert.pixabaysearchengine.ui.imagesscreen


import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class TestImagesRecyclerAdapterClickListenerImplTestVersion {

    @get:Rule
    var rule = HiltAndroidRule(this)

    private lateinit var listenerImpl: ImagesRecyclerAdapterClickListenerImplTestVersion

    @Before
    fun initializeListenerImpl(){
        listenerImpl = ImagesRecyclerAdapterClickListenerImplTestVersion()
    }

    @Test
    fun isDataResponseSuccessful(){
        listenerImpl.downloadUrlOnClick("https://raw.githubusercontent.com/mutkuensert/Files/main/test_image.jpg")
        assert(listenerImpl.responseWillBeCheckedInTest.isSuccessful)
    }
}