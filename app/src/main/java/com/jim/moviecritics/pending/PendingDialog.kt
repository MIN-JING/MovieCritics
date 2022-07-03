package com.jim.moviecritics.pending


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
import androidx.navigation.fragment.findNavController
import com.jim.moviecritics.MainViewModel
import com.jim.moviecritics.NavigationDirections
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.DialogPendingBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.ext.showToast
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.jim.moviecritics.pending.PendingViewModel.Companion.INVALID_FORMAT_CAST_EMPTY
import com.jim.moviecritics.pending.PendingViewModel.Companion.INVALID_FORMAT_HIT_EMPTY
import com.jim.moviecritics.pending.PendingViewModel.Companion.INVALID_FORMAT_LEISURE_EMPTY
import com.jim.moviecritics.pending.PendingViewModel.Companion.INVALID_FORMAT_MUSIC_EMPTY
import com.jim.moviecritics.pending.PendingViewModel.Companion.INVALID_FORMAT_STORY_EMPTY
import com.jim.moviecritics.pending.PendingViewModel.Companion.NO_ONE_KNOWS
import com.jim.moviecritics.pending.PendingViewModel.Companion.SCORE_IS_FILLED


class PendingDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<PendingViewModel> { getVmFactory(PendingDialogArgs.fromBundle(requireArguments()).movie) }
    private lateinit var binding: DialogPendingBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //***** Let layout showing match constraint *****
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.PendingDialog)
        if (viewModel.user.value == null) {
            val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
            mainViewModel.user.value?.let { viewModel.takeDownUser(it) }
            Logger.i("Pending mainViewModel.user.value = ${mainViewModel.user.value}")
            Logger.i("Pending viewModel.user.value = ${viewModel.user.value}")
            viewModel.initToggleAndScore()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = DialogPendingBinding.inflate(inflater, container, false)
        binding.layoutPending.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_slide_up))
//        dialog?.setCancelable(true)
//        dialog?.setCanceledOnTouchOutside(true)


        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.movie.observe(viewLifecycleOwner) {
            Logger.i("Pending Dialog movie = $it")
        }

        viewModel.user.observe(viewLifecycleOwner) {
            Logger.i("Pending Dialog user = $it")
        }

//        viewModel.score.observe(viewLifecycleOwner) {
//            Logger.i("Pending Dialog score = $it")
//        }


        viewModel.invalidScore.observe(viewLifecycleOwner) {
            Logger.i("viewModel.invalidScore.value = $it")
            it?.let {
                when (it) {
                    INVALID_FORMAT_LEISURE_EMPTY -> {
                        activity.showToast("Leisure minimum score is 0.5 star, please try rating again.")
                    }
                    INVALID_FORMAT_HIT_EMPTY -> {
                        activity.showToast("Hit minimum score is 0.5 star, please try rating again.")
                    }
                    INVALID_FORMAT_CAST_EMPTY -> {
                        activity.showToast("Cast minimum score is 0.5 star, please try rating again.")
                    }
                    INVALID_FORMAT_MUSIC_EMPTY -> {
                        activity.showToast("Music minimum score is 0.5 star, please try rating again.")
                    }
                    INVALID_FORMAT_STORY_EMPTY -> {
                        activity.showToast("Story minimum score is 0.5 star, please try rating again.")
                    }
                    NO_ONE_KNOWS -> {
                        Logger.i("Unknown invalidScore value NO_ONE_KNOWS = $it")
                    }
                    SCORE_IS_FILLED -> {
                        activity.showToast("The movie's score was published !")
                    }
                }
            }
        }


        viewModel.leave.observe(viewLifecycleOwner) {
//                it?.let {
//                    dismiss()
//                    viewModel.onLeaveCompleted()
//                }
            when (viewModel.leave.value) {
                true -> {
                    dismiss()
                    viewModel.onLeaveCompleted()
//                    Toast.makeText(context, "The movie's score was published !", Toast.LENGTH_LONG).show()
                }
//                false -> Toast.makeText(context, "5個評分構面最低分數為0.5顆星等，請重新選擇", Toast.LENGTH_LONG).show()
//                null -> Logger.i("PendingViewModel.leave.value = null")
                else -> {}
            }
        }

        viewModel.navigateToReview.observe(viewLifecycleOwner) {
            Logger.i("PendingViewModel.navigateToReview = $it")
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToReviewDialog(it))
                viewModel.onReviewNavigated()
            }
        }

        viewModel.leisurePending.observe(viewLifecycleOwner) {
            Logger.i("Pending Dialog leisurePending = $it")
        }

        viewModel.hitPending.observe(viewLifecycleOwner) {
            Logger.i("Pending Dialog hitPending = $it")
        }

        viewModel.castPending.observe(viewLifecycleOwner) {
            Logger.i("Pending Dialog castPending = $it")
        }

        viewModel.musicPending.observe(viewLifecycleOwner) {
            Logger.i("Pending Dialog musicPending = $it")
        }

        viewModel.storyPending.observe(viewLifecycleOwner) {
            Logger.i("Pending Dialog storyPending = $it")
        }

//        viewModel.isFillScore.observe(viewLifecycleOwner, Observer {
//            if (viewModel.isFillScore.value == false) {
//                Toast.makeText(context, "5個評分構面最低分數為0.5顆星等，請重新選擇", Toast.LENGTH_LONG).show()
//            }
//        })



        return binding.root
//        return inflater.inflate(R.layout.fragment_pending, container, false)
    }

    override fun dismiss() {
        binding.layoutPending.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_slide_down))
//        Handler().postDelayed({ super.dismiss() }, 200)
        lifecycleScope.launch {
            delay(200)
            super.dismiss()
        }
    }
}