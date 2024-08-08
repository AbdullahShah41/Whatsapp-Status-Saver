package com.example.whatsappstatussaver.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.whatsappstatussaver.R
import com.example.whatsappstatussaver.databinding.FragmentSavedImageViewerBinding


class SavedImageViewerFragment : Fragment() {
    private val binding: FragmentSavedImageViewerBinding by lazy {
        FragmentSavedImageViewerBinding.inflate(layoutInflater)
    }

//    private val args: SavedImageViewerFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val imageUri = args.imageUri

    }
}