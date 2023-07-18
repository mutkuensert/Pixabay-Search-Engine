package com.mutkuensert.pixabaysearchengine.data.model.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mutkuensert.pixabaysearchengine.data.model.image.ImageHitsModel
import com.mutkuensert.pixabaysearchengine.data.model.video.VideoHitsModel
import com.mutkuensert.pixabaysearchengine.data.service.RequestService
import com.mutkuensert.pixabaysearchengine.data.source.ImagesPagingSource
import com.mutkuensert.pixabaysearchengine.data.source.VideosPagingSource
import com.mutkuensert.pixabaysearchengine.domain.ImageRequestModel
import com.mutkuensert.pixabaysearchengine.domain.Repository
import com.mutkuensert.pixabaysearchengine.domain.VideoRequestModel
import com.mutkuensert.pixabaysearchengine.libraries.appscope.AppScope
import com.mutkuensert.pixabaysearchengine.util.Resource
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.flow.Flow

class RepositoryImpl @Inject constructor(
    private val requestService: RequestService,
    private val appScope: AppScope
) : Repository {

    override suspend fun requestBackgroundImage(): Resource<ImageHitsModel> {
        val response = requestService.searchImageRequest(
            search = "landscape",
            colors = "white",
            //editorsChoice = true,
            perPage = 200
        )

        if (response.isSuccessful) {
            val hitsList = response.body()?.hits
            hitsList?.let {
                val random =
                    Random(System.nanoTime()).nextInt(it.size) //Seeded Random: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.random/-random.html
                return Resource.success(it[random])
            }
        }

        return Resource.error("Error", null)
    }

    override suspend fun requestImages(imageRequestModel: ImageRequestModel): Flow<PagingData<ImageHitsModel>> {
        return Pager(
            PagingConfig(pageSize = imageRequestModel.perPage)
        ) {
            ImagesPagingSource(requestImages = {
                requestService.searchImageRequest(
                    search = imageRequestModel.search,
                    imageType = imageRequestModel.imageType,
                    orientation = imageRequestModel.orientation,
                    minWidth = imageRequestModel.minWidth,
                    minHeight = imageRequestModel.minHeight,
                    colors = imageRequestModel.colors,
                    editorsChoice = imageRequestModel.editorsChoice,
                    safeSearch = imageRequestModel.safeSearch,
                    order = imageRequestModel.order,
                    page = it,
                    perPage = imageRequestModel.perPage
                )
            })
        }.flow.cachedIn(appScope)
    }

    override suspend fun requestVideos(videoRequestModel: VideoRequestModel): Flow<PagingData<VideoHitsModel>> {
        return Pager(
            PagingConfig(pageSize = videoRequestModel.perPage)
        ) {
            VideosPagingSource(requestVideos = {
                requestService.searchVideoRequest(
                    search = videoRequestModel.search,
                    videoType = videoRequestModel.videoType,
                    minWidth = videoRequestModel.minWidth,
                    minHeight = videoRequestModel.minHeight,
                    editorsChoice = videoRequestModel.editorsChoice,
                    safeSearch = videoRequestModel.safeSearch,
                    order = videoRequestModel.order,
                    page = it,
                    perPage = videoRequestModel.perPage
                )
            })
        }.flow.cachedIn(appScope)
    }
}