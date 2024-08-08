package com.example.whatsappstatussaver.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.whatsappstatussaver.fragments.BusinessImagesFragment
import com.example.whatsappstatussaver.fragments.BusinessVideosFragment

private const val NUM_TABS = 2

class BusinessViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle)  {
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return BusinessImagesFragment()
        }
        return BusinessVideosFragment()
    }
}
