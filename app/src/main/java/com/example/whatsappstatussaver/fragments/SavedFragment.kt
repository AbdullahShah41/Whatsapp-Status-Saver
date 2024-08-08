package com.example.whatsappstatussaver.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.whatsappstatussaver.R
import com.example.whatsappstatussaver.adapters.ImageAdapter
import com.example.whatsappstatussaver.adapters.SavedViewPagerAdapter
import com.example.whatsappstatussaver.adapters.VideoAdapter
import com.example.whatsappstatussaver.adapters.ViewPagerAdapter
import com.example.whatsappstatussaver.data.ModelImageUri
import com.example.whatsappstatussaver.data.ModelVideoUri
import com.example.whatsappstatussaver.databinding.FragmentHomeBinding
import com.example.whatsappstatussaver.databinding.FragmentSavedBinding
import com.example.whatsappstatussaver.utils.tabViewList
import com.example.whatsappstatussaver.viewmodels.SavedStatusViewModel
import com.example.whatsappstatussaver.viewmodels.VideosViewModel
import com.google.android.material.tabs.TabLayoutMediator


class SavedFragment : Fragment() {
    private lateinit var binding: FragmentSavedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("SavedFragment", "onCreateView: Called")
        binding = FragmentSavedBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SavedFragment", "onViewCreated: Called")

        val customText = requireActivity().findViewById<TextView>(R.id.cs_text)
        customText.text = getString(R.string.saved_statusess)

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val svAdapter = SavedViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = svAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabViewList[position] }.attach()

        Log.d("SavedFragment", "ViewModel initialized")


//







        Log.d("SavedFragment", "Calling loadSavedStatus")

    }

    private fun listItemClicked(status: Any) {

    }
    private fun fabClicked(status: Any){

    }


}


