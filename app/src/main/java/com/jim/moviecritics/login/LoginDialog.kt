package com.jim.moviecritics.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.jim.moviecritics.MainViewModel
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.DialogLoginBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<LoginViewModel> { getVmFactory() }
    private lateinit var binding: DialogLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //***** Let layout showing match constraint *****
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.LoginDialog)
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

        val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        viewModel.user.observe(viewLifecycleOwner) {
            it?.let {
                mainViewModel.setupUser(it)
            }
        }

        viewModel.leave.observe(viewLifecycleOwner) {
            it?.let {
                dismiss()
                viewModel.onLeaveCompleted()
            }
        }

        viewModel.navigateToLoginSuccess.observe(viewLifecycleOwner) {
            it?.let {

            }
        }

        viewModel.loginGoogle.observe(viewLifecycleOwner) {
            it?.let {
                Logger.i("viewModel.loginGoogle = $it")
            }
        }

        return binding.root
    }


    override fun dismiss() {
        binding.layoutLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_slide_down))
        lifecycleScope.launch {
            delay(200)
            super.dismiss()
            viewModel.onLeaveCompleted()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        viewModel.fbCallbackManager.onActivityResult(requestCode, resultCode, data)

//        if (RC_REQUEST_PERMISSION_SUCCESS_CONTINUE_FILE_CREATION == requestCode) {
//            saveToDriveAppFolder();
//        }
    }

}