package com.example.whatsappstatussaver.Adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsappstatussaver.Data.ImageUri
import com.example.whatsappstatussaver.databinding.ImageItemBinding

class ImageAdapter(private val images: List<ImageUri>,private val clickListener: (ImageUri) -> Unit) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

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
            .into(holder.binding.imageView)

        holder.itemView.setOnClickListener {
            clickListener(images[position])
        }
    }

    inner class ImageViewHolder(val binding: ImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}





