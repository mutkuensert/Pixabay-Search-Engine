package com.mutkuensert.pixabaysearchengine.ui.searchscreen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutkuensert.pixabaysearchengine.data.ImageHitsModel
import com.mutkuensert.pixabaysearchengine.data.ImagesModel
import com.mutkuensert.pixabaysearchengine.data.source.ImagesRepository
import com.mutkuensert.pixabaysearchengine.data.source.RequestService
import com.mutkuensert.pixabaysearchengine.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

private const val TAG = "SearchScreenFragmentViewModel"

@HiltViewModel
class SearchScreenFragmentViewModel @Inject constructor(private val imagesRepository: ImagesRepository) : ViewModel() {

    val data = MutableLiveData<Resource<ImagesModel>>(Resource.standby(null))
    val backgroundImageData = MutableLiveData<Resource<ImageHitsModel>>(Resource.standby(null))

    fun requestSearchScreenBackgroundImage() {
        backgroundImageData.value = Resource.loading(null)
        viewModelScope.launch(Dispatchers.IO){
            backgroundImageData.postValue(imagesRepository.requestBackgroundImage())
        }
    }

}