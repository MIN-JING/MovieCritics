package com.jim.moviecritics.login

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jim.moviecritics.MainViewModel
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.DialogLoginBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class LoginDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<LoginViewModel> { getVmFactory() }
    private lateinit var binding: DialogLoginBinding

    private lateinit var googleSignInClient: GoogleSignInClient
//    private lateinit var firebaseAuth: FirebaseAuth
//    private lateinit var account: GoogleSignInAccount

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//            handleSignInResult(task)
            viewModel.handleSignInResult(task)
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //***** Let layout showing match constraint *****
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.LoginDialog)

//        UserManager.userToken = null

        // Google log in
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient =
            context?.let { GoogleSignIn.getClient(it, googleSignInOptions) }
            ?: throw NullPointerException("Expression 'context?.let { GoogleSignIn.getClient(it, gso) }' must not be null")
        // Google log out
//        googleSignInClient.signOut()

        // Firebase auth
//        firebaseAuth = Firebase.auth
        // Firebase log out
//        Firebase.auth.signOut()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = DialogLoginBinding.inflate(inflater, container, false)
        binding.layoutLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_slide_up))

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.buttonLoginGoogle.setOnClickListener {
            signInGoogle()
        }

        val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        viewModel.liveUser.observe(viewLifecycleOwner) {
            Logger.i("Login Dialog viewModel.user = $it")
            it?.let {
                mainViewModel.setupUser(it)
                viewModel.userSignIn(it)
            }
        }

        viewModel.leave.observe(viewLifecycleOwner) {
            it?.let {
                dismiss()
                viewModel.onLeaveCompleted()
            }
        }

//        viewModel.loginGoogle.observe(viewLifecycleOwner) {
//            it?.let {
//                Logger.i("viewModel.loginGoogle = $it")
//                signInGoogle()
//                viewModel.onLoginGoogleCompleted()
//            }
//        }

        viewModel.navigateToLoginSuccess.observe(viewLifecycleOwner) {
            it?.let {
                mainViewModel.navigateToLoginSuccess(it)
                dismiss()
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
//        val account = context?.let { GoogleSignIn.getLastSignedInAccount(it) }
        // if account != null , then updateUI(account)
//        Logger.i("Google account = $account")

    }

    override fun dismiss() {
        binding.layoutLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_slide_down))
        lifecycleScope.launch {
            delay(200)
            super.dismiss()
            viewModel.onLeaveCompleted()
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
//        startActivityForResult(signInIntent, RC_SIGN_IN)
        launcher.launch(signInIntent)
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            handleSignInResult(task)
//        }
//    }

//    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
//        try {
//            account = completedTask.getResult(ApiException::class.java)
//            val googleId = account.id ?: ""
//            Logger.i("Google ID = $googleId")
//            val googleFirstName = account.givenName ?: ""
//            Logger.i("Google First Name = $googleFirstName")
//            val googleLastName = account.familyName ?: ""
//            Logger.i("Google Last Name = $googleLastName")
//            val googleEmail = account.email ?: ""
//            Logger.i("Google Email = $googleEmail")
//            val googleProfilePicURL = account.photoUrl.toString()
//            Logger.i("Google Profile Pic URL = $googleProfilePicURL")
//            val googleIdToken = account.idToken ?: ""
//            Logger.i("Google ID Token = $googleIdToken")
//            val googleIsExpired = account.isExpired
//            Logger.i("Google isExpired = $googleIsExpired")
//
//            account.idToken?.let { firebaseAuthWithGoogle(it) }
//
//        } catch (e: ApiException) {
//            // Sign in was unsuccessful
//            Logger.e("Google log in failed code = ${e.statusCode}")
//        }
//
//        viewModel.user.value?.name = account.givenName + "" + account.familyName
//        viewModel.user.value?.email = account.email.toString()
//        viewModel.user.value?.pictureUri = account.photoUrl.toString()
//
//    }

//    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        firebaseAuth.signInWithCredential(credential)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Logger.i("signInWithCredential:success")
//                    val user = firebaseAuth.currentUser
//                    Logger.i("signInWithCredential user = $user")
//                    Logger.i("signInWithCredential user.providerId = ${user?.providerId}")
//                    Logger.i("signInWithCredential user.tenantId = ${user?.tenantId}")
//                    Logger.i("signInWithCredential user.uid = ${user?.uid}")
//                    val firebaseTokenResult = user?.getIdToken(false)?.result
//                    Logger.i("signInWithCredential user.getIdToken.result.token = ${firebaseTokenResult?.token}")
//                    Logger.i("signInWithCredential user.getIdToken.result.expirationTimestamp = ${firebaseTokenResult?.expirationTimestamp}")
//                    Logger.i("signInWithCredential user.getIdToken.result.signInProvider = ${firebaseTokenResult?.signInProvider}")
//
//                    viewModel.user.value?.firebaseToken = firebaseTokenResult?.token.toString()
//
//                    val firebaseDate = firebaseTokenResult?.expirationTimestamp?.let { Date(it) }
//
//                    if (firebaseDate != null) {
//                        viewModel.user.value?.firebaseTokenExpiration = Timestamp(firebaseDate)
//                    }
//
//                    viewModel.user.value?.signInProvider = firebaseTokenResult?.signInProvider.toString()
//
//                    Logger.i("Login Dialog viewModel.user OBSERVE = ${viewModel.user.value}")
//
//                    UserManager.userToken = firebaseTokenResult?.token.toString()
//
//
//                } else {
//                    Logger.w("signInWithCredential:failure e = ${task.exception}")
//                }
//            }
//    }

//    companion object {
//        const val RC_SIGN_IN = 9001
//    }

}