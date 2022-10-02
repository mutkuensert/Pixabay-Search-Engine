package com.mutkuensert.pixabaysearchengine.ui.imagesscreen

import android.view.View
import com.bumptech.glide.Glide
import com.mutkuensert.pixabaysearchengine.R

//The MyDownloaderClassTestVersion() parameter in ImagesRecyclerAdapter is not important.
//MyDownloaderClassTestVersion will be tested separately.
class FakeImagesRecyclerAdapter: ImagesRecyclerAdapter(MyDownloaderTestVersion()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        with(holder.binding){
            imageView.setOnClickListener {
                imageInfos.visibility = if(imageInfos.visibility == View.GONE){ View.VISIBLE } else { View.GONE }
            }

            progressBar.visibility = View.GONE
            Glide
                .with(imageView.context)
                .load(R.drawable.test_image)
                .into(holder.binding.imageView)
        }
    }
}