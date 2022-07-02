package com.jim.moviecritics.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jim.moviecritics.databinding.FragmentProfileBinding
import com.jim.moviecritics.ext.getVmFactory


class ProfileFragment : Fragment() {

    private val viewModel by viewModels<ProfileViewModel> { getVmFactory(ProfileFragmentArgs.fromBundle(requireArguments()).userKey) }

//    companion object {
//        fun newInstance() = ProfileFragment()
//    }
//
//    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

//        val binding = FragmentProfileBinding.inflate(inflater, container, false)
//        binding.lifecycleOwner = viewLifecycleOwner

        val tabLayoutArray = arrayOf("Guide", "Favorite")

        FragmentProfileBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewpagerProfile.let {
//                tabsProfile.setupWithViewPager(it)
                it.adapter = ProfilePagerAdapter(childFragmentManager, lifecycle)
                TabLayoutMediator(tabsProfile, it) { tab, position ->
                    tab.text = tabLayoutArray[position]
                }.attach()
            }
            return@onCreateView root
        }

//        binding.viewpagerProfile.adapter = ProfilePagerAdapter(childFragmentManager, lifecycle)

//        TabLayoutMediator(binding.tabsProfile, binding.viewpagerProfile) { tab, position ->
//            tab.text = tabLayoutArray[position]
//        }.attach()

//        TabLayoutMediator.TabConfigurationStrategy()



//        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}