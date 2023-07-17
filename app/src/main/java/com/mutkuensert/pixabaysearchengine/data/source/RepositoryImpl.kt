package com.mutkuensert.pixabaysearchengine.data.source

import com.mutkuensert.pixabaysearchengine.data.model.image.ImageHitsModel
import com.mutkuensert.pixabaysearchengine.domain.ImageRequestModel
import com.mutkuensert.pixabaysearchengine.data.model.image.ImagesModel
import com.mutkuensert.pixabaysearchengine.data.model.video.MainVideosModel
import com.mutkuensert.pixabaysearchengine.domain.VideoRequestModel
import com.mutkuensert.pixabaysearchengine.domain.Repository
import com.mutkuensert.pixabaysearchengine.util.Resource
import javax.inject.Inject
import kotlin.random.Random

class RepositoryImpl @Inject constructor(private val requestService: RequestService) : Repository {

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

    override suspend fun requestImages(imageRequestModel: ImageRequestModel): Resource<ImagesModel> {
        val response = requestService.searchImageRequest(
            search = imageRequestModel.search,
            imageType = imageRequestModel.imageType,
            orientation = imageRequestModel.orientation,
            minWidth = imageRequestModel.minWidth,
            minHeight = imageRequestModel.minHeight,
            colors = imageRequestModel.colors,
            editorsChoice = imageRequestModel.editorsChoice,
            safeSearch = imageRequestModel.safeSearch,
            order = imageRequestModel.order,
            page = imageRequestModel.page,
            perPage = imageRequestModel.perPage
        )
        if (response.isSuccessful) {
            return Resource.success(response.body())
        }

        return Resource.error("Error.", null)
    }

    override suspend fun requestVideos(videoRequestModel: VideoRequestModel): Resource<MainVideosModel> {
        videoRequestModel.also {
            val response = requestService.searchVideoRequest(
                search = it.search,
                videoType = it.videoType,
                minWidth = it.minWidth,
                minHeight = it.minHeight,
                editorsChoice = it.editorsChoice,
                safeSearch = it.safeSearch,
                order = it.order,
                page = it.page,
                perPage = it.perPage
            )
            if (response.isSuccessful) {
                return Resource.success(response.body())
            }
        }

        return Resource.error("Error.", null)
    }
}