package com.jim.moviecritics.pending


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
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

class PendingDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<PendingViewModel> { getVmFactory(PendingDialogArgs.fromBundle(requireArguments()).movie) }
    private lateinit var binding: DialogPendingBinding

//    companion object {
//        fun newInstance() = PendingFragment()
//    }
//
//    private lateinit var viewModel: PendingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //***** Let layout showing match constraint *****
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.PendingDialog)

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

        viewModel.movie.observe(viewLifecycleOwner, Observer {
            Logger.i("Pending Dialog movie = $it")
//            it.imdbID?.let { it1 -> viewModel.pushWatchedMovie(imdbID = it1, userID = 790926) }
        })

        viewModel.user.observe(viewLifecycleOwner, Observer {
            Logger.i("Pending Dialog user = $it")
        })

        viewModel.isWatch.observe(viewLifecycleOwner, Observer {
            Logger.i("Pending Dialog checkWatch = $it")
        })

        viewModel.invalidScore.observe(viewLifecycleOwner, Observer {
            Logger.i("viewModel.invalidScore.value = ${viewModel.invalidScore.value}")
            it?.let {
                when (it) {
                    INVALID_FORMAT_LEISURE_EMPTY -> {
                        activity.showToast("Leisure 最低分數為0.5顆星等，請重新選擇")
                    }
                    INVALID_FORMAT_HIT_EMPTY -> {
                        activity.showToast("Hit 最低分數為0.5顆星等，請重新選擇")
                    }
                    INVALID_FORMAT_CAST_EMPTY -> {
                        activity.showToast("Cast 最低分數為0.5顆星等，請重新選擇")
                    }
                    INVALID_FORMAT_MUSIC_EMPTY -> {
                        activity.showToast("Music 最低分數為0.5顆星等，請重新選擇")
                    }
                    INVALID_FORMAT_STORY_EMPTY -> {
                        activity.showToast("Story 最低分數為0.5顆星等，請重新選擇")
                    }
                    else -> { Logger.i("Unknown invalidScore value = $it") }
                }
            }
        })


        viewModel.leave.observe(viewLifecycleOwner, Observer {
//                it?.let {
//                    dismiss()
//                    viewModel.onLeaveCompleted()
//                }
            when (viewModel.leave.value) {
                true -> {
                    dismiss()
                    viewModel.onLeaveCompleted()
                    Toast.makeText(context, "The movie's score was published !", Toast.LENGTH_LONG).show()
                }
                false -> Toast.makeText(context, "5個評分構面最低分數為0.5顆星等，請重新選擇", Toast.LENGTH_LONG).show()

                null -> Logger.i("viewModel.leave.value = null")
            }
        })

        viewModel.leisurePending.observe(viewLifecycleOwner, Observer {
            Logger.i("Pending Dialog leisurePending = $it")
        })

        viewModel.hitPending.observe(viewLifecycleOwner, Observer {
            Logger.i("Pending Dialog hitPending = $it")
        })

        viewModel.castPending.observe(viewLifecycleOwner, Observer {
            Logger.i("Pending Dialog castPending = $it")
        })

        viewModel.musicPending.observe(viewLifecycleOwner, Observer {
            Logger.i("Pending Dialog musicPending = $it")
        })

        viewModel.storyPending.observe(viewLifecycleOwner, Observer {
            Logger.i("Pending Dialog storyPending = $it")
        })

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

//    fun leave() { dismiss() }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(PendingViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.i("Pending Dialog onDestroy()")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Logger.i("Pending Dialog onDestroyView()")
    }

}