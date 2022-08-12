package com.jim.moviecritics.profile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.FragmentProfileBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.Logger

class ProfileFragment : Fragment() {

    private val profileViewModel by viewModels<ProfileViewModel> {
        getVmFactory(ProfileFragmentArgs.fromBundle(requireArguments()).userKey)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu_log_out, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_log_out -> {
                Logger.i("toolbar_button_log_out onClick")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        setHasOptionsMenu(true)

        FragmentProfileBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = profileViewModel

            viewpagerProfile.let {
                it.adapter = ProfilePagerAdapter(childFragmentManager, lifecycle)
                TabLayoutMediator(tabsProfile, it) { tab, position ->
                    tab.text = ProfileTypeFilter.values()[position].value
                }.attach()
            }
            return@onCreateView root
        }
    }
}
