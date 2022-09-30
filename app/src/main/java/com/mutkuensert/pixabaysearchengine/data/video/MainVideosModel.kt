package com.mutkuensert.pixabaysearchengine.data.video


data class MainVideosModel(
    val total: Int?,
    val totalHits: Int?,
    val hits: List<VideoHitsModel>?
)
