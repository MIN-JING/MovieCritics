package com.jim.moviecritics


import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.jim.moviecritics.databinding.ActivityMainBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.CurrentFragmentType
import kotlinx.coroutines.launch


class MainActivity : BaseActivity() {

    val viewModel by viewModels<MainViewModel> { getVmFactory() }
    private lateinit var binding: ActivityMainBinding

    // get the height of status bar from system
    private val statusBarHeight: Int
        get() {
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            return when {
                resourceId > 0 -> resources.getDimensionPixelSize(resourceId)
                else -> 0
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.currentFragmentType.observe(this) {
                Log.i("Jim","~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                Log.i("Jim","[${viewModel.currentFragmentType.value}]")
                Log.i("Jim","~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
        }

        setupToolbar()
        setupBottomNav()
        setupNavController()
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

    private fun setupBottomNav() {
        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {

                    findNavController(R.id.navHostFragment).navigate(NavigationDirections.navigateToHomeFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_search-> {

                    findNavController(R.id.navHostFragment).navigate(NavigationDirections.navigateToSearchFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_watchlist-> {

                    when (viewModel.isLoggedIn) {
                        true -> {
                            findNavController(R.id.navHostFragment).navigate(
                                NavigationDirections.navigateToWatchlistFragment(viewModel.user.value)
                            )
                        }
                        false -> {
                            findNavController(R.id.navHostFragment).navigate(NavigationDirections.navigationToLoginDialog())
                            return@setOnItemSelectedListener false
                        }
                    }
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_profile -> {

                    when (viewModel.isLoggedIn) {
                        true -> {
                            findNavController(R.id.navHostFragment).navigate(
                                NavigationDirections.navigateToProfileFragment(viewModel.user.value)
                            )
                        }
                        false -> {
                            findNavController(R.id.navHostFragment).navigate(NavigationDirections.navigationToLoginDialog())
                            return@setOnItemSelectedListener false
                        }
                    }
                    return@setOnItemSelectedListener true
                }
            }
            false
        }

//        val menuView = binding.bottomNavView.getChildAt(0) as BottomNavigationMenuView
//        val itemView = menuView.getChildAt(2) as BottomNavigationItemView
//        val bindingBadge = BadgeBottomBinding.inflate(LayoutInflater.from(this), itemView, true)
//        bindingBadge.lifecycleOwner = this
//        bindingBadge.viewModel = viewModel
    }

    private fun setupToolbar() {

        binding.toolbar.setPadding(0, statusBarHeight, 0, 0)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        launch {

            val dpi = resources.displayMetrics.densityDpi.toFloat()
            val dpiMultiple = dpi / DisplayMetrics.DENSITY_DEFAULT

            val cutoutHeight = getCutoutHeight()

            Log.i("Jim","====== ${Build.MODEL} ======")
            Log.i("Jim","$dpi dpi (${dpiMultiple}x)")
            Log.i("Jim","statusBarHeight: ${statusBarHeight}px/${statusBarHeight / dpiMultiple}dp")

            when {
                cutoutHeight > 0 -> {
                    Log.i("Jim","cutoutHeight: ${cutoutHeight}px/${cutoutHeight / dpiMultiple}dp")

                    val oriStatusBarHeight = resources.getDimensionPixelSize(R.dimen.height_status_bar_origin)

                    binding.toolbar.setPadding(0, oriStatusBarHeight, 0, 0)
                    val layoutParams = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT)
                    layoutParams.gravity = Gravity.CENTER

                    when (Build.MODEL) {
                        "Pixel 5" -> { Log.i("Jim","Build.MODEL is ${Build.MODEL}") }
                        else -> { layoutParams.topMargin = statusBarHeight - oriStatusBarHeight }
                    }
//                    binding.imageToolbarLogo.layoutParams = layoutParams
                    binding.textToolbarTitle.layoutParams = layoutParams
                }
            }
            Log.i("Jim","====== ${Build.MODEL} ======")
        }
    }
}