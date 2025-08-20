package com.jim.moviecritics.profile

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.google.android.material.tabs.TabLayoutMediator
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.FragmentProfileBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.Logger

class ProfileFragment : Fragment() {

    private val profileViewModel by viewModels<ProfileViewModel> {
        getVmFactory(ProfileFragmentArgs.fromBundle(requireArguments()).userKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        FragmentProfileBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = profileViewModel

            viewpagerProfile.let {
                it.adapter = ProfilePagerAdapter(childFragmentManager, lifecycle)
                TabLayoutMediator(tabsProfile, it) { tab, position ->
                    tab.text = ProfileTypeFilter.entries[position].value
                }.attach()
            }
            return@onCreateView root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu_log_out, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.toolbar_log_out -> {
                        Logger.i("toolbar_button_log_out onClick")
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}
