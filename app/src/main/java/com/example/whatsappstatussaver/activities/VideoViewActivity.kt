package com.example.whatsappstatussaver.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsappstatussaver.databinding.ActivityVideoViewBinding

class VideoViewActivity : AppCompatActivity() {

    private lateinit var binding : ActivityVideoViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val videoUri: Uri? = intent.getParcelableExtra("videoUri")
        Log.d("VideoViewActivity", "Intent Data: ${intent.extras}")

        if (videoUri != null) {
            Log.d("VideoViewActivity", "Received Video URI: $videoUri")
            val mediaController = MediaController(this)
            mediaController.setAnchorView(binding.videoView)
            binding.videoView.setMediaController(mediaController)
            binding.videoView.setVideoURI(videoUri)
            binding.videoView.requestFocus()
            binding.videoView.start()
        } else{
            Log.d("VideoViewActivity", "No Video URI received")
        }
    }

}


