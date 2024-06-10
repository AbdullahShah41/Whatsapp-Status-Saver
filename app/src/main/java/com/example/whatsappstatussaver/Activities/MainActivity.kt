package com.example.whatsappstatussaver.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.whatsappstatussaver.Adapters.ViewPagerAdapter
import com.example.whatsappstatussaver.R
import com.example.whatsappstatussaver.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

val compArray = arrayOf(
    "images",
    "videos"
)

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = ViewPagerAdapter( supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = compArray[position]
        }.attach()
    }
}