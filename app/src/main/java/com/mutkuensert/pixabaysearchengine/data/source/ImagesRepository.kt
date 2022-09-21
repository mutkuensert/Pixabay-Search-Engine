package com.mutkuensert.pixabaysearchengine.data.source

import android.content.Context
import com.mutkuensert.pixabaysearchengine.data.ImageHitsModel
import com.mutkuensert.pixabaysearchengine.data.ImageRequestModel
import com.mutkuensert.pixabaysearchengine.data.ImagesModel
import com.mutkuensert.pixabaysearchengine.util.Resource
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import kotlin.random.Random

open class ImagesRepository @Inject constructor (
    @ApplicationContext context: Context
){

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ImagesRepoEntryPoint{
        fun requestService(): RequestService
    }

    private val requestService = EntryPointAccessors.fromApplication(context, ImagesRepoEntryPoint::class.java).requestService()

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
}