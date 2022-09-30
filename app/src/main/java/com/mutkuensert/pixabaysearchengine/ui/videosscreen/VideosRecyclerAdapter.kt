package com.mutkuensert.pixabaysearchengine.ui.videosscreen

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mutkuensert.pixabaysearchengine.data.video.VideoHitsModel
import com.mutkuensert.pixabaysearchengine.databinding.SingleVideoItemBinding

private const val VIDEO_SIZE = "960x540"
class VideosRecyclerAdapter: ListAdapter<VideoHitsModel, VideosRecyclerAdapter.ViewHolder>(VideoHitsModelListDiffCallback) {

    class ViewHolder(val binding: SingleVideoItemBinding): RecyclerView.ViewHolder(binding.root){

    }

    object VideoHitsModelListDiffCallback: DiffUtil.ItemCallback<VideoHitsModel>(){
        override fun areItemsTheSame(oldItem: VideoHitsModel, newItem: VideoHitsModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VideoHitsModel, newItem: VideoHitsModel): Boolean {
            return oldItem.pageURL == newItem.pageURL
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleVideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding){

            val ownerText = "Owner: " + getItem(position).user
            ownerNameTextView.text = ownerText


            val videoThumbnailImageUrl = "https://i.vimeocdn.com/video/${getItem(position).pictureID}_${VIDEO_SIZE}.jpg"

            Glide
                .with(imageView.context)
                .load(videoThumbnailImageUrl)
                .listener(object: RequestListener<Drawable> { //https://stackoverflow.com/a/54130621
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        videoInfosAndButtons.visibility = View.VISIBLE
                        return false
                    }

                })
                .into(holder.binding.imageView)
        }
    }
}