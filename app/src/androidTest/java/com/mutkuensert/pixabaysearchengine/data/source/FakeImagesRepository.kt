package com.mutkuensert.pixabaysearchengine.data.source

import androidx.test.core.app.ApplicationProvider
import com.mutkuensert.pixabaysearchengine.data.ImageHitsModel
import com.mutkuensert.pixabaysearchengine.data.ImageRequestModel
import com.mutkuensert.pixabaysearchengine.data.ImagesModel
import com.mutkuensert.pixabaysearchengine.util.Resource
import kotlinx.coroutines.delay

class FakeImagesRepository(): ImagesRepository(ApplicationProvider.getApplicationContext()) {
    private var lastId = 0

    override suspend fun requestBackgroundImage(): Resource<ImageHitsModel> {
        delay(1000L)
        val hit = ImageHitsModel(
                    id = null,
                    pageUrl = null,
                    type = null,
                    tags = null,
                    previewURL = null,
                    previewWidth = null,
                    previewHeight = null,
                    webFormatURL = null,
                    webFormatWidth = null,
                    webFormatHeight = null,
                    largeImageURL = null,
                    fullHDURL = null,
                    imageURL = null,
                    imageWidth = null,
                    imageHeight = null,
                    imageSize = null,
                    views = null,
                    downloads = null,
                    likes = null,
                    comments = null,
                    userId = null,
                    user = null,
                    userImageURL = null,
                )
        return Resource.success(hit)
    }

    override suspend fun requestImages(imageRequestModel: ImageRequestModel): Resource<ImagesModel> {
        val hits = mutableListOf<ImageHitsModel>()
        for(i in lastId..lastId+2){
            hits.add(
                ImageHitsModel(
                    id = i,
                    pageUrl = null,
                    type = null,
                    tags = null,
                    previewURL = null,
                    previewWidth = null,
                    previewHeight = null,
                    webFormatURL = null,
                    webFormatWidth = null,
                    webFormatHeight = null,
                    largeImageURL = i.toString(), //DiffUtil checks the contents.
                    fullHDURL = null,
                    imageURL = null,
                    imageWidth = null,
                    imageHeight = null,
                    imageSize = null,
                    views = null,
                    downloads = null,
                    likes = null,
                    comments = null,
                    userId = null,
                    user = null,
                    userImageURL = null,
                ))
        }
        lastId += 3
        val imagesModel = ImagesModel(null, null, hits)

        return Resource.success(imagesModel)
    }

}