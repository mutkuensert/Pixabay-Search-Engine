package com.mutkuensert.pixabaysearchengine.domain

import com.mutkuensert.pixabaysearchengine.data.model.image.ImageHitsModel
import com.mutkuensert.pixabaysearchengine.data.model.image.ImagesModel
import com.mutkuensert.pixabaysearchengine.data.model.video.MainVideosModel
import com.mutkuensert.pixabaysearchengine.util.Resource

interface Repository {
    suspend fun requestBackgroundImage(): Resource<ImageHitsModel>
    suspend fun requestImages(imageRequestModel: ImageRequestModel): Resource<ImagesModel>
    suspend fun requestVideos(videoRequestModel: VideoRequestModel): Resource<MainVideosModel>
}