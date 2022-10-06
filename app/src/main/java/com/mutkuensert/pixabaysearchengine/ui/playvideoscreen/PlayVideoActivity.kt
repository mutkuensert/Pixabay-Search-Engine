package com.mutkuensert.pixabaysearchengine.ui.playvideoscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mutkuensert.pixabaysearchengine.databinding.ActivityPlayVideoBinding

class PlayVideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}