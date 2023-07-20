package com.mutkuensert.pixabaysearchengine.feature.video.playvideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.mutkuensert.pixabaysearchengine.databinding.ActivityPlayVideoBinding
import com.mutkuensert.pixabaysearchengine.util.KEY_VIDEO_URL

class PlayVideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayVideoBinding
    private var player: ExoPlayer? = null
    private lateinit var videoUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        videoUrl = intent.extras?.getString(KEY_VIDEO_URL).toString()
        initializePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this)
            .build()

        player!!.apply {
            binding.videoView.player = this
            playWhenReady = true
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
        }
    }
}