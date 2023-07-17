package com.mutkuensert.pixabaysearchengine.data.model.video

import com.squareup.moshi.Json

data class VideoHitsModel(
    val id: Int,
    val pageURL: String,
    val type: String,
    val tags: String,
    val duration: Long,

    @Json(name = "picture_id")
    val pictureID: String,

    val videos: VideosModel,
    val views: Long,
    val downloads: Long,
    val likes: Long,
    val comments: Long,

    @Json(name = "user_id")
    val userID: Long,

    val user: String,
    val userImageURL: String
)
