package com.mutkuensert.pixabaysearchengine.data

data class ImagesModel(
    val total: Int?,
    val totalHits: Int?,
    val hits: List<ImageHitsModel>?
)
