package com.jim.moviecritics.review


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.DialogReviewBinding
import com.jim.moviecritics.ext.getVmFactory
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

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val binding = DialogReviewBinding.inflate(inflater, container, false)
        binding.layoutReview.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_slide_up))

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.movie.observe(viewLifecycleOwner, Observer {
            Logger.i("Review Dialog movie = $it")
            Logger.i("Review Dialog movie.awards = ${it.awards}")
            Logger.i("Review Dialog movie.genres = ${it.genres}")
        })

        viewModel.leave.observe(viewLifecycleOwner, Observer {
            dismiss()
        })

        return binding.root
//        return inflater.inflate(R.layout.fragment_review, container, false)
    }

    override fun dismiss() {
//    if (::binding.isInitialized) { }

            binding = DialogReviewBinding.inflate(LayoutInflater.from(context))

            binding.layoutReview.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_slide_down))
            lifecycleScope.launch {
                delay(200)
                super.dismiss()
                viewModel.onLeaveCompleted()
            }
    }

}