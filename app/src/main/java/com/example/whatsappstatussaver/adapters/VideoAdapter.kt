package com.example.whatsappstatussaver.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsappstatussaver.R
import com.example.whatsappstatussaver.data.ModelVideoUri
import com.example.whatsappstatussaver.databinding.VideoItemBinding

class VideoAdapter(
    private val videos: List<ModelVideoUri>,
    private val fabClickListener: (ModelVideoUri) -> Unit,
    private val listItemClicked: (ModelVideoUri) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = VideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]
        Glide.with(holder.itemView.context)
            .load(videos[position].videoUri)
            .placeholder(R.drawable.shimmer)
            .into(holder.binding.videoView)
        holder.itemView.setOnClickListener {
            listItemClicked(videos[position])
        }
        holder.binding.fab.setOnClickListener {
            fabClickListener(video)
        }
    }


    inner class VideoViewHolder(val binding: VideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}






