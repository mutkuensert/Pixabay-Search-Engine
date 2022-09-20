package com.mutkuensert.pixabaysearchengine.ui.imagesscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutkuensert.pixabaysearchengine.data.ImageRequestModel
import com.mutkuensert.pixabaysearchengine.data.ImagesModel
import com.mutkuensert.pixabaysearchengine.data.source.ImagesRepository
import com.mutkuensert.pixabaysearchengine.data.source.RequestService
import com.mutkuensert.pixabaysearchengine.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImagesScreenViewModel @Inject constructor(private val repository: ImagesRepository) : ViewModel() {

    private val _data = MutableLiveData<Resource<ImagesModel>>(Resource.standby(null))
    val data get() = _data

    fun requestImages(imageRequestModel: ImageRequestModel){
        _data.value = Resource.loading(null)

        viewModelScope.launch(Dispatchers.IO){
            _data.postValue(repository.requestImages(imageRequestModel))
        }
    }

}