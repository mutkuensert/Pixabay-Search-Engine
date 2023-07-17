package com.mutkuensert.pixabaysearchengine.feature.playvideoscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.mutkuensert.pixabaysearchengine.databinding.ActivityPlayVideoBinding

class PlayVideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayVideoBinding
    private var player: ExoPlayer? = null
    private lateinit var videoUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        videoUrl = intent.extras?.getString("videoUrl").toString()
        initializePlayer()
    }

    private fun initializePlayer(){
        player = ExoPlayer.Builder(this)
            .build().also { exoPlayer ->
            binding.videoView.player = exoPlayer
                val mediaItem = MediaItem.fromUri(videoUrl)
                exoPlayer.setMediaItem(mediaItem)
            }
    }
}