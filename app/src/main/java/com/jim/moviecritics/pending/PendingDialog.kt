package com.jim.moviecritics.pending


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
import com.jim.moviecritics.databinding.DialogPendingBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

        viewModel.leave.observe(viewLifecycleOwner, Observer {
                it?.let {
                    dismiss()
                    viewModel.onLeaveCompleted()
                }
        })


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

}