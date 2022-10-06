package com.mutkuensert.pixabaysearchengine.data.source

import com.mutkuensert.pixabaysearchengine.data.image.ImageHitsModel
import com.mutkuensert.pixabaysearchengine.data.image.ImageRequestModel
import com.mutkuensert.pixabaysearchengine.data.image.ImagesModel
import com.mutkuensert.pixabaysearchengine.data.video.MainVideosModel
import com.mutkuensert.pixabaysearchengine.data.video.VideoRequestModel
import com.mutkuensert.pixabaysearchengine.util.Resource
import kotlin.random.Random

open class Repository(private val requestService: RequestService){

    open suspend fun requestBackgroundImage(): Resource<ImageHitsModel>{

        val response = requestService.searchImageRequest(
            search =  "landscape",
            colors = "white",
            //editorsChoice = true,
            perPage = 200
        )

        if(response.isSuccessful){
            val hitsList = response.body()?.hits
            hitsList?.let {
                val random = Random(System.nanoTime()).nextInt(it.size) //Seeded Random: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.random/-random.html
                return Resource.success(it[random])
            }
        }
         //ELSE
        return Resource.error("Error", null)
    }

    open suspend fun requestImages(imageRequestModel: ImageRequestModel): Resource<ImagesModel>{
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
        if(response.isSuccessful){
            return Resource.success(response.body())
        }

        //ELSE
        return Resource.error("Error.", null)
    }

    open suspend fun  requestVideos(videoRequestModel: VideoRequestModel): Resource<MainVideosModel>{
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
            if(response.isSuccessful){
                return Resource.success(response.body())
            }
        }
        return Resource.error("Error.", null)
    }
}