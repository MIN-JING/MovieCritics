package com.jim.moviecritics

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.jim.moviecritics.databinding.ActivityMainBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.CurrentFragmentType
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.launch


class MainActivity : BaseActivity() {
//    AppCompatActivity()

    val viewModel by viewModels<MainViewModel> { getVmFactory() }
    private lateinit var binding: ActivityMainBinding

    private lateinit var mGoogleSignInClient: GoogleSignInClient

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
//        setContentView(R.layout.activity_main)

//        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
//            this, R.layout.activity_main
//        )

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.currentFragmentType.observe(
            this,
            Observer {
                Log.i("Jim","~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                Log.i("Jim","[${viewModel.currentFragmentType.value}]")
                Log.i("Jim","~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
            }
        )

//        val navController: NavController = Navigation.findNavController(
//            this, R.id.activity_main_nav_host_fragment
//        )
        setupToolbar()
        setupBottomNav()
        setupNavController()

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.buttonMainGoogle.setOnClickListener {
            Logger.i("buttonMainGoogle onClick")
            signInGoogle()
        }


    }

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        // if account != null , then updateUI(account)
        Logger.i("Google account = $account")

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
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

                    findNavController(R.id.navHostFragment).navigate(NavigationDirections.navigateToWatchlistFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_profile -> {

                    when (viewModel.isLoggedIn) {
                        true -> {
                            findNavController(R.id.navHostFragment).navigate(
                                NavigationDirections.navigateToProfileFragment(
                                    //viewModel.user.value
                                )
                            )
                        }
                        false -> {
                            findNavController(R.id.navHostFragment).navigate(NavigationDirections.navigationToLoginDialog())
                            return@setOnItemSelectedListener false
                        }
                    }
//                    findNavController(R.id.navHostFragment).navigate(NavigationDirections.navigateToProfileFragment())
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

    private fun signInGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val googleId = account?.id ?: ""
            Logger.i("Google ID = $googleId")
            val googleFirstName = account?.givenName ?: ""
            Logger.i("Google First Name = $googleFirstName")
            val googleLastName = account?.familyName ?: ""
            Logger.i("Google Last Name = $googleLastName")
            val googleEmail = account?.email ?: ""
            Logger.i("Google Email = $googleEmail")
            val googleProfilePicURL = account?.photoUrl.toString()
            Logger.i("Google Profile Pic URL = $googleProfilePicURL")
            val googleIdToken = account?.idToken ?: ""
            Logger.i("Google ID Token = $googleIdToken")

        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Logger.e("failed code = ${e.statusCode.toString()}")
        }
    }

    companion object {
        const val RC_SIGN_IN = 9001
    }
}
