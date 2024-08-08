package com.example.whatsappstatussaver.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.whatsappstatussaver.R
import com.example.whatsappstatussaver.adapters.VideoAdapter
import com.example.whatsappstatussaver.data.ModelVideoUri
import com.example.whatsappstatussaver.databinding.FragmentSavedVideoBinding
import com.example.whatsappstatussaver.viewmodels.SavedStatusViewModel

class SavedVideoFragment : Fragment() {
    private lateinit var binding: FragmentSavedVideoBinding
    private lateinit var viewModel: SavedStatusViewModel
    private lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentSavedVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[SavedStatusViewModel::class.java]

        videoAdapter = VideoAdapter(
            emptyList(),
            { videoUri -> fabClicked(videoUri) },
            { videoUri -> listItemClicked(videoUri) })
        Log.d("SavedFragment", "VideoAdapter initialized")

        binding.recyclerViewVideos.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerViewVideos.adapter = videoAdapter

        viewModel.savedVideos.observe(viewLifecycleOwner){videos->
            videoAdapter.updateVideos(videos)
        }
        viewModel.loadSavedStatus()
    }

    private fun listItemClicked(videoUri: ModelVideoUri) {


    }

    private fun fabClicked(videoUri: ModelVideoUri) {


    }
}
