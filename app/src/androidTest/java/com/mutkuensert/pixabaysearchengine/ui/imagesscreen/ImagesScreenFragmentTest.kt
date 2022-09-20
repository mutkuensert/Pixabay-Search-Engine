package com.mutkuensert.pixabaysearchengine.ui.imagesscreen

import com.mutkuensert.pixabaysearchengine.R
import com.mutkuensert.pixabaysearchengine.data.ImageRequestModel
import com.mutkuensert.pixabaysearchengine.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ImagesScreenFragmentTest {

    @get: Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun imagesScreenFragmentShowsImagesProperly(){
        val bundle = ImagesScreenFragmentArgs(ImageRequestModel()).toBundle() //ImageRequestModel isn't important. FakeImagesRepository creates its own request while faking it.
        launchFragmentInHiltContainer<ImagesScreenFragment>(bundle, R.style.Theme_PixabaySearchEngine)
        Thread.sleep(20000L)
    }
}