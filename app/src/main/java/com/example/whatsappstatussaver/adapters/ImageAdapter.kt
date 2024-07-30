package com.example.whatsappstatussaver.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsappstatussaver.R
import com.example.whatsappstatussaver.data.ModelImageUri
import com.example.whatsappstatussaver.databinding.ImageItemBinding

class ImageAdapter(
    private var images: List<ModelImageUri>,
    private val imagesClickListener: (ModelImageUri) -> Unit,
    private val fabClickListener: (ModelImageUri) -> Unit,
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {

        val binding = ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(images[position].imageUri)
            .placeholder(R.drawable.shimmer)
            .into(holder.binding.imageView)


        holder.binding.imageView.setOnClickListener {
            imagesClickListener(images[position])
        }
        holder.binding.fab.setOnClickListener {
            fabClickListener(images[position])
        }
    }
    fun updateImages(newImages: List<ModelImageUri>) {
        images = newImages
        notifyDataSetChanged()
    }

    inner class ImageViewHolder(val binding: ImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}










