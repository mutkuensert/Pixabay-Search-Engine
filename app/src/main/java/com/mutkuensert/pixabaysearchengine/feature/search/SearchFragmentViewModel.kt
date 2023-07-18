package com.mutkuensert.pixabaysearchengine.feature.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutkuensert.pixabaysearchengine.data.model.image.ImageHitsModel
import com.mutkuensert.pixabaysearchengine.data.model.image.ImagesModel
import com.mutkuensert.pixabaysearchengine.data.model.repository.RepositoryImpl
import com.mutkuensert.pixabaysearchengine.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class SearchFragmentViewModel @Inject constructor(private val repositoryImpl: RepositoryImpl) :
    ViewModel() {
    val data = MutableLiveData<Resource<ImagesModel>>(Resource.standby(null))
    val backgroundImageData = MutableLiveData<Resource<ImageHitsModel>>(Resource.standby(null))

    fun requestSearchScreenBackgroundImage() {
        backgroundImageData.value = Resource.loading(null)
        viewModelScope.launch(Dispatchers.IO) {
            backgroundImageData.postValue(repositoryImpl.requestBackgroundImage())
        }
    }
}