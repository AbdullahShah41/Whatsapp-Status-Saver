package com.example.whatsappstatussaver.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.whatsappstatussaver.viewmodels.ImagesViewModel
import com.example.whatsappstatussaver.viewmodelsfactories.ImagesViewModelFactory
import com.example.whatsappstatussaver.data.ModelImageUri
import com.example.whatsappstatussaver.databinding.ActivityImageViewerBinding

class ImageViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageViewerBinding
    private val viewModel: ImagesViewModel by viewModels { ImagesViewModelFactory(application) }
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageUri = intent.getParcelableExtra("imageUri")

        if (imageUri != null) {
            Glide.with(this)
                .load(imageUri)
                .into(binding.imageView)
        }
        binding.dbButton.setOnClickListener {
            imageUri?.let { uri ->
                val status = ModelImageUri(uri, System.currentTimeMillis())
                viewModel.saveFile(
                    status,
                    onSuccess = {
                        Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {
                        Toast.makeText(this, "Image can't be saved", Toast.LENGTH_SHORT).show()
                    },
//                    requireActivity()
                    this
                )
            }
        }
        binding.rpButton.setOnClickListener {
            imageUri?.let { uri ->
                repostImage(uri)
            }
        }
    }
    private fun repostImage(uri: Uri){
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/jpeg"
        }
        startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }
}
