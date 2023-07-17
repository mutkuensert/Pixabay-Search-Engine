package com.mutkuensert.pixabaysearchengine.feature.videosscreen

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutkuensert.pixabaysearchengine.data.model.video.MainVideosModel
import com.mutkuensert.pixabaysearchengine.domain.Repository
import com.mutkuensert.pixabaysearchengine.domain.VideoRequestModel
import com.mutkuensert.pixabaysearchengine.libraries.downloader.Downloader
import com.mutkuensert.pixabaysearchengine.libraries.downloader.MimeDataType
import com.mutkuensert.pixabaysearchengine.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class VideosFragmentViewModel @Inject constructor(
    private val repository: Repository,
    private val downloader: Downloader,
) : ViewModel() {
    private val _data = MutableLiveData<Resource<MainVideosModel>>(Resource.standby(null))
    val data get() = _data

    init {
        downloader.setFileFormatExtractor {
            it.substringAfterLast(".").substringBefore("?")
        }

        downloader.setMimeDataType(MimeDataType.VIDEO)
    }

    fun downloadUrl(url: String) {
        downloader.downloadUrl(url)
    }

    fun requestVideos(videoRequestModel: VideoRequestModel) {
        _data.value = Resource.loading(null)
        viewModelScope.launch(Dispatchers.IO) {
            _data.postValue(repository.requestVideos(videoRequestModel))
        }
    }

    fun initDownloaderActivityResultLauncher(activityResultLauncher: Downloader.() -> ActivityResultLauncher<Intent>?) {
        downloader.initActivityResultLauncher {
            activityResultLauncher.invoke(this)
        }
    }
}