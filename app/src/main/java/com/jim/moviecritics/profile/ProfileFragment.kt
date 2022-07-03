package com.jim.moviecritics.profile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.jim.moviecritics.MainViewModel
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.FragmentProfileBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.Logger


class ProfileFragment : Fragment() {

    private val profileViewModel by viewModels<ProfileViewModel> { getVmFactory(ProfileFragmentArgs.fromBundle(requireArguments()).userKey) }

//    private lateinit var googleSignInClient: GoogleSignInClient
//    companion object {
//        fun newInstance() = ProfileFragment()
//    }
//
//    private lateinit var viewModel: ProfileViewModel

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu_log_out, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_log_out -> {
                Logger.i("toolbar_button_log_out onClick")

//                Firebase.auth.signOut()
//
//                val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                    .requestIdToken(getString(R.string.server_client_id))
//                    .requestEmail()
//                    .build()
//
//                googleSignInClient =
//                    context?.let { GoogleSignIn.getClient(it, googleSignInOptions) }
//                        ?: throw NullPointerException("Expression 'context?.let { GoogleSignIn.getClient(it, gso) }' must not be null")
////                googleSignInClient.signOut()
//                googleSignInClient.signOut().addOnCompleteListener {
//                    val intent = Intent(context, LoginDialog::class.java)
//                }

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

//        val binding = FragmentProfileBinding.inflate(inflater, container, false)
//        binding.lifecycleOwner = viewLifecycleOwner
        setHasOptionsMenu(true)

        if (profileViewModel.user.value == null) {
            val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
            profileViewModel.user.observe(viewLifecycleOwner) {
                if (null != it) {
                    Logger.i("Profile Fragment mainViewModel.setupUser(it)")
                    mainViewModel.setupUser(it)
                }
            }
        }

        val tabLayoutArray = arrayOf("Guide", "Favorite")

        FragmentProfileBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
//            viewModel = viewModel
            viewModel = profileViewModel
            viewpagerProfile.let {
//                tabsProfile.setupWithViewPager(it)
                it.adapter = ProfilePagerAdapter(childFragmentManager, lifecycle)
                TabLayoutMediator(tabsProfile, it) { tab, position ->
                    tab.text = tabLayoutArray[position]
                }.attach()
            }
            return@onCreateView root
        }

//        binding.viewModel = viewModel



//        binding.viewpagerProfile.adapter = ProfilePagerAdapter(childFragmentManager, lifecycle)

//        TabLayoutMediator(binding.tabsProfile, binding.viewpagerProfile) { tab, position ->
//            tab.text = tabLayoutArray[position]
//        }.attach()

//        TabLayoutMediator.TabConfigurationStrategy()


//        return binding.root
//        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}