package com.example.whatsappstatussaver.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappstatussaver.Adapters.ViewPagerAdapter
import com.example.whatsappstatussaver.Data.ImageUri
import com.example.whatsappstatussaver.R
import com.example.whatsappstatussaver.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder.Permission
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.PermissionRequest
import java.util.jar.Manifest

val compArray = arrayOf(
    "images",
    "videos"
)

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var rvstatusList: RecyclerView
    private lateinit var statusList: ArrayList<ImageUri>

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

//        supportActionBar!!.title="All Status"

    }
}

