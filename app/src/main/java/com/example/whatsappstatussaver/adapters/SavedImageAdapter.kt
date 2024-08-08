package com.example.whatsappstatussaver.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsappstatussaver.R
import com.example.whatsappstatussaver.data.ModelImageUri
import com.example.whatsappstatussaver.databinding.SavedItemBinding

class SavedImageAdapter(
    private var images: List<ModelImageUri>,
    private val imagesClickListener: (ModelImageUri) -> Unit,
) : RecyclerView.Adapter<SavedImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {

        val binding = SavedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedImageAdapter.ImageViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(images[position].imageUri)
            .placeholder(R.drawable.shimmer)
            .into(holder.binding.imageView)


        holder.binding.imageView.setOnClickListener {
            imagesClickListener(images[position])
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun updateImages(newImages: List<ModelImageUri>) {
        images = newImages
        notifyDataSetChanged()
    }
    inner class ImageViewHolder(val binding: SavedItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}
