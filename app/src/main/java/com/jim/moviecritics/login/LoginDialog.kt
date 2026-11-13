package com.jim.moviecritics.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.WindowCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.jim.moviecritics.MainViewModel
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.DialogLoginBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.InsetMode
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.applySystemBarInsets
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<LoginViewModel> { getVmFactory() }
    private lateinit var binding: DialogLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ***** Let layout showing match constraint *****
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.LoginDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = DialogLoginBinding.inflate(inflater, container, false)
        binding.layoutLogin.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.anim_slide_up)
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.buttonLoginGoogle.setOnClickListener {
            viewModel.signInWithGoogle(requireActivity())
        }

        val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        viewModel.liveUser.observe(viewLifecycleOwner) {
            Logger.i("Login Dialog viewModel liveUser = $it")
            it?.let {
                Logger.i("Login Dialog mainViewModel.setupUser(it)")
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
                Logger.i("Login Dialog viewModel.navigateToLoginSuccess = $it")
                mainViewModel.navigateToLoginSuccess(it)
                dismiss()
            }
        }

        viewModel.statusLogIn.observe(viewLifecycleOwner) {
            Logger.i("Login Dialog viewModel statusLogIn = $it")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.let { window ->
            // 1. Tell the window to draw behind the system bars
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }

        // Apply the system navigation bar height as PADDING to the dialog's root view
        applySystemBarInsets(
            view = binding.layoutLogin,
            mode = InsetMode.PADDING,
            applyTop = false,
            applyBottom = true
        )
    }

    override fun dismiss() {
        binding.layoutLogin.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.anim_slide_down)
        )
        lifecycleScope.launch {
            delay(200)
            super.dismiss()
            viewModel.onLeaveCompleted()
        }
    }
}
