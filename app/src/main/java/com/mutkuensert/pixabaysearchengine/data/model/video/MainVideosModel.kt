package com.mutkuensert.pixabaysearchengine.data.model.video

data class MainVideosModel(
    val total: Int?,
    val totalHits: Int?,
    val hits: List<VideoHitsModel>?
)
