package com.example.whatsappstatussaver.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsappstatussaver.data.ModelVideoUri
import com.example.whatsappstatussaver.databinding.ActivityVideoViewBinding
import com.example.whatsappstatussaver.viewmodels.VideosViewModel
import com.example.whatsappstatussaver.viewmodelsfactories.VideosViewModelFactory

class VideoViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoViewBinding
    private val viewModel: VideosViewModel by viewModels { VideosViewModelFactory(application) }
    private var videoUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoUri = intent.getParcelableExtra("videoUri")
        Log.d("VideoViewActivity", "Intent Data: ${intent.extras}")

        if (videoUri != null) {
            Log.d("VideoViewActivity", "Received Video URI: $videoUri")
            val mediaController = MediaController(this)
            mediaController.setAnchorView(binding.videoView)
            binding.videoView.setMediaController(mediaController)
            binding.videoView.setVideoURI(videoUri)
            binding.videoView.requestFocus()
            binding.videoView.start()
        } else {
            Log.d("VideoViewActivity", "No Video URI received")
        }
        binding.dbButton.setOnClickListener {
            videoUri?.let { uri ->
                val status = ModelVideoUri(uri, System.currentTimeMillis())
                viewModel.saveFile(
                    status,
                    onSuccess = {
                        Toast.makeText(this, "Video Saved", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {
                        Toast.makeText(this, "Video can't be saved ", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
        binding.rpButton.setOnClickListener {
            videoUri?.let { uri ->
                repostVideo(uri)
            }
        }
    }
    private fun repostVideo(uri: Uri) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "video/jpeg"
        }
        startActivity(Intent.createChooser(shareIntent, "Share Video"))
    }
}


