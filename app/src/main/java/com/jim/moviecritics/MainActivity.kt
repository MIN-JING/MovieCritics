package com.jim.moviecritics

import android.os.Bundle
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.jim.moviecritics.databinding.ActivityMainBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.login.UserManager
import com.jim.moviecritics.util.CurrentFragmentType
import com.jim.moviecritics.util.InsetMode
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.applySystemBarInsets

class MainActivity : AppCompatActivity() {

    private companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> { getVmFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Enable edge-to-edge display to draw behind system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 2. Inflate your layout and set the content view
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // 3. Apply the insets! This is the best place.
        // Apply top inset as padding to a root container that scrolls
        applySystemBarInsets(
            view = binding.toolbar,
            mode = InsetMode.MARGIN,
            applyTop = true,
            applyBottom = false // Toolbar doesn't need bottom inset
        )

        // Apply top inset as margin to a non-scrolling Toolbar
        applySystemBarInsets(
            view = binding.bottomNavView,
            mode = InsetMode.MARGIN,
            applyTop = false,
            applyBottom = true
        )

        setupToolbar()
        setupBottomNavOnItemSelectedListener()
        setupNavController()

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.currentFragmentType.observe(this) {
            Logger.i("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
            Logger.i("[${viewModel.currentFragmentType.value}]")
            Logger.i("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
        }

        viewModel.navigateToLoginSuccess.observe(this) {
            it?.let {
                viewModel.onLoginSuccessNavigated()

                when (viewModel.currentFragmentType.value) {
                    CurrentFragmentType.PROFILE -> viewModel.navigateToProfileByBottomNav(it)
                    else -> viewModel.navigateToProfileByBottomNav(it)
                }
            }
        }

        viewModel.navigateToProfileByBottomNav.observe(this) {
            it?.let {
                binding.bottomNavView.selectedItemId = R.id.navigation_profile
                viewModel.onProfileNavigated()
            }
        }

        viewModel.user.observe(this) {
            Logger.i("MainViewModel.user = $it")
            UserManager.user = it
        }
    }

    private fun setupToolbar() {
        val layoutParams = Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER
        binding.textToolbarTitle.layoutParams = layoutParams
    }

    private fun setupNavController() {
        findNavController(R.id.navHostFragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.homeFragment -> CurrentFragmentType.HOME
                R.id.searchFragment -> CurrentFragmentType.SEARCH
                R.id.watchlistFragment -> CurrentFragmentType.WATCHLIST
                R.id.profileFragment -> CurrentFragmentType.PROFILE
                R.id.detailFragment -> CurrentFragmentType.DETAIL
                else -> viewModel.currentFragmentType.value
            }
        }
    }

    private fun setupBottomNavOnItemSelectedListener() {
        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {

                    findNavController(R.id.navHostFragment)
                        .navigate(NavigationDirections.navigateToHomeFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_search -> {

                    findNavController(R.id.navHostFragment)
                        .navigate(NavigationDirections.navigateToSearchFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_watchlist -> {

                    when (viewModel.isLoggedIn) {
                        true -> {
                            viewModel.checkUser()
                            findNavController(R.id.navHostFragment).navigate(
                                NavigationDirections
                                    .navigateToWatchlistFragment(viewModel.user.value)
                            )
                        }

                        false -> {
                            findNavController(R.id.navHostFragment)
                                .navigate(NavigationDirections.navigationToLoginDialog())
                            return@setOnItemSelectedListener false
                        }
                    }
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_profile -> {

                    when (viewModel.isLoggedIn) {
                        true -> {
                            viewModel.checkUser()
                            findNavController(R.id.navHostFragment).navigate(
                                NavigationDirections.navigateToProfileFragment(viewModel.user.value)
                            )
                        }

                        false -> {
                            findNavController(R.id.navHostFragment)
                                .navigate(NavigationDirections.navigationToLoginDialog())
                            return@setOnItemSelectedListener false
                        }
                    }
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}
