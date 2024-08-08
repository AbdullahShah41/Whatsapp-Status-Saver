package com.example.whatsappstatussaver.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.whatsappstatussaver.R
import com.example.whatsappstatussaver.adapters.ImageAdapter
import com.example.whatsappstatussaver.adapters.SavedImageAdapter
import com.example.whatsappstatussaver.data.ModelImageUri
import com.example.whatsappstatussaver.databinding.FragmentSavedImageBinding
import com.example.whatsappstatussaver.viewmodels.SavedStatusViewModel


class SavedImageFragment : Fragment() {
    private lateinit var binding: FragmentSavedImageBinding
    private lateinit var viewModel: SavedStatusViewModel
    private lateinit var imageAdapter: SavedImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentSavedImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[SavedStatusViewModel::class.java]

        imageAdapter = SavedImageAdapter(
            emptyList()
        ) { imageUri -> listItemClicked(imageUri) }
        Log.d("SavedFragment", "ImageAdapter initialized")

        binding.recyclerViewImages.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerViewImages.adapter = imageAdapter
        Log.d("SavedFragment", "RecyclerView initialized")

        viewModel.savedImage.observe(viewLifecycleOwner) {images ->
            Log.d("SavedFragment", "Observer: Statuses updated, count = ${images.size}")
            imageAdapter.updateImages(images)
        }
        viewModel.loadSavedStatus()
    }

    private fun listItemClicked(imageUri: ModelImageUri) {
//        val action = SavedImageFragmentDirections.
//            actionSavedImageFragmentToSavedImageViewerFragment(imageUri.imageUri.toString())
//            findNavController().navigate(action)
    }
}
