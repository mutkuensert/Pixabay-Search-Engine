package com.mutkuensert.pixabaysearchengine.data

import com.squareup.moshi.Json

data class ImageHitsModel(
    val id: Int?,
    val pageUrl: String?,
    val type: String?,
    val tags: String?,
    val previewURL: String?,
    val previewWidth: Int?,
    val previewHeight: Int?,
    val webFormatURL: String?,
    val webFormatWidth: Int?,
    val webFormatHeight: Int?,
    val largeImageURL: String?,
    val fullHDURL: String?,
    val imageURL: String?,
    val imageWidth: Int?,
    val imageHeight: Int?,
    val imageSize: Int?,
    val views: Int?,
    val downloads: Int?,
    val likes: Int?,
    val comments: Int?,
    @Json(name = "user_id") val userId: Int?,
    val user: String?,
    val userImageURL: String?
)
