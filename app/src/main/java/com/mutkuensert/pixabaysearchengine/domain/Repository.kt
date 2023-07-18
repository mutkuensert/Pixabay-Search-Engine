package com.mutkuensert.pixabaysearchengine.domain

import androidx.paging.PagingData
import com.mutkuensert.pixabaysearchengine.data.model.image.ImageHitsModel
import com.mutkuensert.pixabaysearchengine.data.model.video.MainVideosModel
import com.mutkuensert.pixabaysearchengine.util.Resource
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun requestBackgroundImage(): Resource<ImageHitsModel>
    suspend fun requestImages(imageRequestModel: ImageRequestModel): Flow<PagingData<ImageHitsModel>>
    suspend fun requestVideos(videoRequestModel: VideoRequestModel): Resource<MainVideosModel>
}