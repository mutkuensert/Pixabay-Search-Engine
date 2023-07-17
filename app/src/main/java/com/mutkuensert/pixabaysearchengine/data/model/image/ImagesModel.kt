package com.mutkuensert.pixabaysearchengine.data.model.image

data class ImagesModel(
    val total: Int?,
    val totalHits: Int?,
    val hits: List<ImageHitsModel>?
)
