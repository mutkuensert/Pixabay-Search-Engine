package com.mutkuensert.pixabaysearchengine.data.image

import com.mutkuensert.pixabaysearchengine.data.image.ImageHitsModel

data class ImagesModel(
    val total: Int?,
    val totalHits: Int?,
    val hits: List<ImageHitsModel>?
)
