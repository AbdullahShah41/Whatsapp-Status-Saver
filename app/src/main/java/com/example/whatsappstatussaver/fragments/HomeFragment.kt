package com.example.whatsappstatussaver.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.whatsappstatussaver.R
import com.example.whatsappstatussaver.adapters.ViewPagerAdapter
import com.example.whatsappstatussaver.databinding.FragmentHomeBinding
import com.example.whatsappstatussaver.utils.tabViewList
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val customText = requireActivity().findViewById<TextView>(R.id.cs_text)
        customText.text = getString(R.string.whatsapp_statusess)

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val vAdapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = vAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabViewList[position] }.attach()

        }
    }





