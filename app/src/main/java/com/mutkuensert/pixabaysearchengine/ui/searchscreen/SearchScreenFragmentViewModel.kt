package com.mutkuensert.pixabaysearchengine.ui.searchscreen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutkuensert.pixabaysearchengine.data.ImageHitsModel
import com.mutkuensert.pixabaysearchengine.data.ImagesModel
import com.mutkuensert.pixabaysearchengine.data.RequestService
import com.mutkuensert.pixabaysearchengine.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

private const val TAG = "SearchScreenFragmentViewModel"

@HiltViewModel
class SearchScreenFragmentViewModel @Inject constructor(private val requestService: RequestService) : ViewModel() {

    val data = MutableLiveData<Resource<ImagesModel>>(Resource.standby(null))
    val backgroundImageData = MutableLiveData<Resource<ImageHitsModel>>(Resource.standby(null))

    fun requestSearchScreenBackgroundImage() {

        backgroundImageData.value = Resource.loading(null)
        viewModelScope.launch(Dispatchers.IO){
            val response = requestService.searchImageRequest(
                search =  "landscape",
                colors = "white",
                editorsChoice = true,
                perPage = 200
            )

            if(response.isSuccessful){
                val hitsList = response.body()?.hits
                hitsList?.let {
                    val random = Random(System.nanoTime()).nextInt(it.size) //Seeded Random: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.random/-random.html
                    backgroundImageData.postValue(Resource.success(it[random]))
                }
            }else {
                backgroundImageData.postValue(Resource.error("Error", null))
                Log.e(TAG, "requestImages(): Response is not successful.")
            }
        }
    }

    fun requestImages(
        search: String,
        imageType: String = "all",
        orientation: String = "all",
        minWidth: Int = 0,
        minHeight: Int = 0,
        editorsChoice: Boolean = false,
        order: String = "popular", // popular, latest
        page: Int = 1,
        perPage: Int = 20, //3-200
    ){
        /*data.value = Resource.loading(null)

        viewModelScope.launch(Dispatchers.IO){
            val call = requestService.searchImageRequest(
                search =  search
            )
            call.execute().also { response ->
                if(response.isSuccessful){
                    data.postValue(Resource.success(response.body()))
                    }else {
                        Resource.error("Error", null)
                        Log.e(TAG, "requestImages(): Response is not successful.")
                    }
                }
            }*/
        }

    }