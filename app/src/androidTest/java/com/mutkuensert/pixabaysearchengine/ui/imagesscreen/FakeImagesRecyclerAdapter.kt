package com.mutkuensert.pixabaysearchengine.ui.imagesscreen

import android.view.View
import com.bumptech.glide.Glide
import com.mutkuensert.pixabaysearchengine.R
import kotlinx.coroutines.runBlocking

class FakeImagesRecyclerAdapter(private val onClickListener: ImagesRecyclerAdapterClickListener) : ImagesRecyclerAdapter(onClickListener) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //the click listener won't be set here. Instead, the download process will be tested separately.

        with(holder.binding){
            progressBar.visibility = View.GONE
            runBlocking {
                Glide
                    .with(imageView.context)
                    .load(R.drawable.test_image)
                    .into(holder.binding.imageView)
            }

        }
    }
}