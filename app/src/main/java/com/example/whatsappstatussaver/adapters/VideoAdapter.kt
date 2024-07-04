package com.example.whatsappstatussaver.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsappstatussaver.data.VideoUri
import com.example.whatsappstatussaver.databinding.VideoItemBinding

class VideoAdapter(private val videos: List<VideoUri>,
                   private val fabClickListener: (VideoUri) -> Unit,
                    private val listItemClicked: (VideoUri) -> Unit
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
            .into(holder.binding.videoView)
        holder.itemView.setOnClickListener {
            listItemClicked(videos[position])
//            val intent = Intent(holder.itemView.context,VideoViewActivity::class.java)
//            intent.putExtra("imageUri",video.videoUri)
//            holder.itemView.context.startActivity(intent)
        }
        holder.binding.fab.setOnClickListener {
            fabClickListener(video)
        }
    }

    inner class VideoViewHolder(val binding: VideoItemBinding):
        RecyclerView.ViewHolder(binding.root){
    }
}






