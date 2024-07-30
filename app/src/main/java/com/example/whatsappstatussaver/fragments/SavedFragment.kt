package com.example.whatsappstatussaver.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.whatsappstatussaver.R
import com.example.whatsappstatussaver.databinding.FragmentHomeBinding
import com.example.whatsappstatussaver.databinding.FragmentSavedBinding


class SavedFragment : Fragment() {
    private lateinit var binding: FragmentSavedBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val customText = requireActivity().findViewById<TextView>(R.id.cs_text)
        customText.text = getString(R.string.saved_statusess)
    }
}

