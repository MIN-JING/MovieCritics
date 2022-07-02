package com.jim.moviecritics.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProfilePagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val titles = arrayOf("Guide", "Favorites")

    override fun getItemCount(): Int {
        return titles.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ItemGuideFragment()
            1 -> ItemFavoriteFragment()
            else -> ItemGuideFragment()
        }
    }


}