package com.mutkuensert.pixabaysearchengine.feature.image

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.mutkuensert.pixabaysearchengine.data.model.image.ImageHitsModel
import com.mutkuensert.pixabaysearchengine.domain.ImageRequestModel
import com.mutkuensert.pixabaysearchengine.domain.Repository
import com.mutkuensert.pixabaysearchengine.libraries.downloader.Downloader
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class ImagesFragmentViewModel @Inject constructor(
    private val repository: Repository,
    private val downloader: Downloader,
) : ViewModel() {
    private var _data: MutableStateFlow<PagingData<ImageHitsModel>> =
        MutableStateFlow(PagingData.empty())
    val data: StateFlow<PagingData<ImageHitsModel>> = _data

    init {
        downloader.setFileFormatExtractor {
            it.substringAfterLast(".").substringBefore("?")
        }
    }

    fun requestImages(imageRequestModel: ImageRequestModel) {
        viewModelScope.launch {
            repository.requestImages(imageRequestModel).collectLatest {
                _data.value = it
            }
        }
    }

    fun downloadUrl(url: String) {
        downloader.downloadUrl(url)
    }

    fun initDownloaderActivityResultLauncher(activityResultLauncher: Downloader.() -> ActivityResultLauncher<Intent>?) {
        downloader.initActivityResultLauncher {
            activityResultLauncher.invoke(this)
        }
    }
}