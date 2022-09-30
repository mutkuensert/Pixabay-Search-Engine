package com.mutkuensert.pixabaysearchengine.ui.videosscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutkuensert.pixabaysearchengine.data.source.Repository
import com.mutkuensert.pixabaysearchengine.data.video.MainVideosModel
import com.mutkuensert.pixabaysearchengine.data.video.VideoRequestModel
import com.mutkuensert.pixabaysearchengine.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideosFragmentViewModel @Inject constructor(private val repository: Repository): ViewModel() {
    private val _data = MutableLiveData<Resource<MainVideosModel>>(Resource.standby(null))
    val data get() = _data

    fun requestVideos(videoRequestModel: VideoRequestModel){
        _data.value = Resource.loading(null)
        viewModelScope.launch(Dispatchers.IO) {
            _data.postValue(repository.requestVideos(videoRequestModel))
        }
    }
}