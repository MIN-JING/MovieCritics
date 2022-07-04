package com.jim.moviecritics.review


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.jim.moviecritics.MainViewModel
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.DialogReviewBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.ext.showToast
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.review.ReviewViewModel.Companion.INVALID_FORMAT_COMMENT_EMPTY
import com.jim.moviecritics.review.ReviewViewModel.Companion.NO_ONE_KNOWS
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReviewDialog : AppCompatDialogFragment()  {

    private val viewModel by viewModels<ReviewViewModel> { getVmFactory(ReviewDialogArgs.fromBundle(requireArguments()).movie) }
    private lateinit var binding: DialogReviewBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //***** Let layout showing match constraint *****
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.ReviewDialog)

        if (viewModel.user.value == null) {
            val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
            mainViewModel.user.value?.let { viewModel.takeDownUser(it) }
            Logger.i("Review mainViewModel.user.value = ${mainViewModel.user.value}")
            Logger.i("Review viewModel.user.value = ${viewModel.user.value}")
        }
        viewModel.initComment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = DialogReviewBinding.inflate(inflater, container, false)
        binding.layoutReview.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_slide_up))

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.movie.observe(viewLifecycleOwner) {
            Logger.i("Review Dialog movie = $it")
            Logger.i("Review Dialog movie.awards = ${it.awards}")
            Logger.i("Review Dialog movie.genres = ${it.genres}")
        }

        viewModel.leave.observe(viewLifecycleOwner) {
            when (viewModel.leave.value) {
                true -> {
                    Logger.i("Review Dialog leave true = $it")
                    dismiss()
                    viewModel.onLeaveCompleted()
                }
                false -> { Logger.i("Review Dialog leave false = $it") }
                null -> { Logger.i("Review Dialog leave null = $it") }
            }
        }

        viewModel.content.observe(viewLifecycleOwner) {
            Logger.i("Review Dialog content = $it")
        }

        viewModel.invalidComment.observe(viewLifecycleOwner) {
            Logger.i("Review Dialog invalidComment = $it")
            it?.let {
                when (it) {
                    INVALID_FORMAT_COMMENT_EMPTY -> {
                        activity.showToast("The content of the review was empty, please try key-in again.")
                    }
                    NO_ONE_KNOWS -> {
                        Logger.i("Unknown invalidComment value NO_ONE_KNOWS = $it")
                    }
                }
            }
        }

        viewModel.status.observe(viewLifecycleOwner) {
            Logger.i("Review Dialog status = $it")
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
//    if (::binding.isInitialized) { }
        binding.layoutReview.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_slide_down))
        lifecycleScope.launch {
            delay(200)
            super.dismiss()
            viewModel.onLeaveCompleted()
        }
    }
}