package com.mutkuensert.pixabaysearchengine.ui.imagesscreen

import com.mutkuensert.pixabaysearchengine.data.ImageHitsModel

interface ImagesRecyclerAdapterClickListener {
    fun onClick(hitItem: ImageHitsModel)
}