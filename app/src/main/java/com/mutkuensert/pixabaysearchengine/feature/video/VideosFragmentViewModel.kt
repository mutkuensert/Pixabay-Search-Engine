package com.mutkuensert.pixabaysearchengine.feature.video

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.mutkuensert.pixabaysearchengine.data.model.video.VideoHitsModel
import com.mutkuensert.pixabaysearchengine.domain.Repository
import com.mutkuensert.pixabaysearchengine.domain.VideoRequestModel
import com.mutkuensert.pixabaysearchengine.libraries.downloader.Downloader
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class VideosFragmentViewModel @Inject constructor(
    private val repository: Repository,
    private val downloader: Downloader,
) : ViewModel() {
    private var _data: MutableStateFlow<PagingData<VideoHitsModel>> =
        MutableStateFlow(PagingData.empty())
    val data: StateFlow<PagingData<VideoHitsModel>> = _data

    init {
        downloader.setFileFormatExtractor {
            it.substringAfterLast(".").substringBefore("?")
        }
    }

    fun downloadUrl(url: String) {
        downloader.downloadUrl(url)
    }

    fun requestVideos(videoRequestModel: VideoRequestModel) {
        viewModelScope.launch {
            repository.requestVideos(videoRequestModel).collectLatest {
                _data.value = it
            }
        }
    }

    fun initDownloaderActivityResultLauncher(activityResultLauncher: Downloader.() -> ActivityResultLauncher<Intent>?) {
        downloader.initActivityResultLauncher {
            activityResultLauncher.invoke(this)
        }
    }
}