package com.example.whatsappstatussaver.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsappstatussaver.adapters.ViewPagerAdapter
import com.example.whatsappstatussaver.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import android.os.Build
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.whatsappstatussaver.R
import com.google.android.material.navigation.NavigationView


val compArray = arrayOf(
    "Images",
    "Videos"
)

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout


        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->

            tab.text = compArray[position]

        }.attach()
        drawerLayout = binding.drawerLayout
        navView = binding.navigationView
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


            navView.setNavigationItemSelectedListener {
                when(it.itemId){
                  R.id.item2 -> {
                      Toast.makeText(this@MainActivity, "item2", Toast.LENGTH_SHORT).show()
                        true
                  }
                    R.id.item3 ->{
                        Toast.makeText(this@MainActivity, "item3", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else->
                        false
                  }


            }
        binding.list.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            true
        }
        else super.onOptionsItemSelected(item)
    }
}




















