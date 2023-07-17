package com.mutkuensert.pixabaysearchengine.feature.imagesscreen

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutkuensert.pixabaysearchengine.domain.ImageRequestModel
import com.mutkuensert.pixabaysearchengine.data.model.image.ImagesModel
import com.mutkuensert.pixabaysearchengine.domain.Repository
import com.mutkuensert.pixabaysearchengine.libraries.downloader.Downloader
import com.mutkuensert.pixabaysearchengine.libraries.downloader.MimeDataType
import com.mutkuensert.pixabaysearchengine.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class ImagesScreenViewModel @Inject constructor(
    private val repository: Repository,
    private val downloader: Downloader,
) : ViewModel() {
    private val _data = MutableLiveData<Resource<ImagesModel>>(Resource.standby(null))
    val data get() = _data

    init {
        downloader.setFileFormatExtractor {
            it.substringAfterLast(".").substringBefore("?")
        }

        downloader.setMimeDataType(MimeDataType.IMAGE)
    }

    fun requestImages(imageRequestModel: ImageRequestModel) {
        _data.value = Resource.loading(null)

        viewModelScope.launch(Dispatchers.IO) {
            _data.postValue(repository.requestImages(imageRequestModel))
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