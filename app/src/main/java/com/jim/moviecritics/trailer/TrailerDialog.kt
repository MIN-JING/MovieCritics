package com.jim.moviecritics.trailer

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.DialogTrailerBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.ext.showToast
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrailerDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<TrailerViewModel> {
        getVmFactory(TrailerDialogArgs.fromBundle(requireArguments()).movie)
    }

    private lateinit var binding: DialogTrailerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ***** Let layout showing match constraint *****
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.TrailerDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = DialogTrailerBinding.inflate(inflater, container, false)
        binding.layoutTrailer.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.anim_slide_up)
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.movie.value?.trailerUri?.let { binding.webViewTrailer.loadUrl(it) }

        // Enable Javascript
        val webSettings = binding.webViewTrailer.settings
        webSettings.javaScriptEnabled = true

        // Force links and redirects to open in the WebView instead of in a browser
        binding.webViewTrailer.webViewClient = WebViewClient()

        binding.webViewTrailer.canGoBack()
        binding.webViewTrailer.setOnKeyListener(
            View.OnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK &&
                    event.action == MotionEvent.ACTION_UP &&
                    binding.webViewTrailer.canGoBack()
                ) {
                    binding.webViewTrailer.goBack()
                    return@OnKeyListener true
                }
                false
            }
        )

        viewModel.movie.observe(viewLifecycleOwner) {
            Logger.i("Review Dialog movie = $it")
        }

        viewModel.leave.observe(viewLifecycleOwner) {
            when (viewModel.leave.value) {
                true -> {
                    Logger.i("Trailer Dialog leave true = $it")
                    dismiss()
                    viewModel.onLeaveCompleted()
                }
                false -> { Logger.i("Trailer Dialog leave false = $it") }
                null -> { Logger.i("Trailer Dialog leave null = $it") }
            }
        }

        viewModel.status.observe(viewLifecycleOwner) {
            Logger.i("Trailer Dialog status = $it")
            it?.let {
                when (it) {
                    LoadApiStatus.DONE -> activity.showToast("The movie's review was published !")
                    else -> { Logger.i("ReviewViewModel status value else = $it") }
                }
            }
        }

        return binding.root
    }

    override fun dismiss() {
        binding.layoutTrailer.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.anim_slide_down)
        )
        lifecycleScope.launch {
            delay(200)
            super.dismiss()
            viewModel.onLeaveCompleted()
        }
    }
}
