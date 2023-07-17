package com.mutkuensert.pixabaysearchengine.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoRequestModel(
    var search: String = "",
    var videoType: String = "all",
    var minWidth: Int = 0,
    var minHeight: Int = 0,
    var editorsChoice: Boolean = false,
    var safeSearch: Boolean = true,
    var order: String = "popular",
    var page: Int = 1,
    var perPage: Int = 20
): Parcelable
