package com.mutkuensert.pixabaysearchengine.data.image

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageRequestModel(
    var search: String = "",
    var imageType: String = "all",
    var orientation: String = "all",
    var minWidth: Int = 0,
    var minHeight: Int = 0,
    var colors: String? = null,
    var editorsChoice: Boolean = false,
    var safeSearch: Boolean = true,
    var order: String = "popular",
    var page: Int = 1,
    var perPage: Int = 20
): Parcelable
