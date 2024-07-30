package com.example.whatsappstatussaver.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.whatsappstatussaver.R
import com.example.whatsappstatussaver.databinding.ActivityMainBinding
import com.example.whatsappstatussaver.fragments.BusinessHomeFragment
import com.example.whatsappstatussaver.fragments.BusinessImagesFragment
import com.example.whatsappstatussaver.fragments.HomeFragment
import com.example.whatsappstatussaver.fragments.ImagesFragment
import com.example.whatsappstatussaver.fragments.SavedFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bottomNavigationView: BottomNavigationView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        toolbar = findViewById(R.id.cs_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = ""

        drawerLayout = binding.drawerLayout
        navView = binding.navigationView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.privacyFragment, R.id.aboutFragment),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        toolbar.navigationIcon = null

        val customIcon = toolbar.findViewById<View>(R.id.hm_icon)
        customIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navController.addOnDestinationChangedListener { _, _, _ ->
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            toolbar.navigationIcon = null
        }

        bottomNavigationView = binding.bottomNav
        bottomNavigationView.setupWithNavController(navController)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.s_wa -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.b_wa -> {
                    navController.navigate(R.id.businessFragment)
                    true
                }

                R.id.save -> {
                    navController.navigate(R.id.saveFragment)
                    true
                }

                else -> false
            }
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.home -> {
                Log.d("MainActivity", "Navigating to HomeFragment")
                navController.navigate(R.id.homeFragment)
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }

            R.id.privacy -> {
                Log.d("MainActivity", "Navigating to PrivacyFragment")
//                findNavController(R.id.nav_host_fragment).navigate(HomeFragmentDirections.actionMainFragmentToPrivacy())
                navController.navigate(R.id.privacyFragment)
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }

            R.id.about -> {
                Log.d("MainActivity", "Navigating to AboutFragment")
//                findNavController(R.id.nav_host_fragment).navigate(HomeFragmentDirections.actionMainFragmentToAboutFragment())
                navController.navigate(R.id.aboutFragment)
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }

            R.id.share -> {
                Log.d("MainActivity", "Navigating to ShareFragment")
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "https://play.google.com/store/apps/details?id=com.example.whatsappstatussaver"
                    )
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(intent, null)
                startActivity(shareIntent)
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }

            R.id.rate -> {
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    data =
                        android.net.Uri.parse("https://play.google.com/store/apps/details?id=com.example.whatsappstatussaver")
                }
                startActivity(intent)
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }

            R.id.b_wa -> {
                Log.d("MainActivity", "Navigating to BusinessHomeFragment")
                navController.navigate(R.id.businessFragment)
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }

            R.id.save -> {
                Log.d("MainActivity", "Navigating to SavedFragment")
                navController.navigate(R.id.saveFragment)
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
        }
        return false
    }


    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager?.fragments?.firstOrNull()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else if (currentFragment is HomeFragment) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}

//
//onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
//    override fun handleOnBackPressed() {
//        val currentFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager?.fragments?.firstOrNull()
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START)
//        } else if (currentFragment is HomeFragment) {
//            finish()
//        } else {
//            // Call the default implementation for the back press
//            super.handleOnBackPressed()
//        }
//    }



