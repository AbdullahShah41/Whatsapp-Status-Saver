package com.example.whatsappstatussaver.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsappstatussaver.Data.VideoUri
import com.example.whatsappstatussaver.databinding.VideoItemBinding

class VideoAdapter(private val videos: List<VideoUri>) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = VideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(videos[position].videoUri)
            .into(holder.binding.videoView)

    }

    inner class VideoViewHolder(val binding: VideoItemBinding):
        RecyclerView.ViewHolder(binding.root){

    }

}