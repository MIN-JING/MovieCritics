package com.jim.moviecritics.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.DialogReportBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.ext.showToast
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReportDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<ReportViewModel> {
        getVmFactory(ReportDialogArgs.fromBundle(requireArguments()).comment)
    }

    private lateinit var binding: DialogReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ***** Let layout showing match constraint *****
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.ReportDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DialogReportBinding.inflate(inflater, container, false)
        binding.layoutReport.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.anim_slide_up)
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.leave.observe(viewLifecycleOwner) {
            it?.let {
                dismiss()
                viewModel.onLeaveCompleted()
            }
        }

        viewModel.invalidReport.observe(viewLifecycleOwner) {
            Logger.i("Report invalidReport = $it")
            it?.let {
                when (it) {
                    ReportViewModel.INVALID_FORMAT_REASON_EMPTY -> {
                        activity.showToast(
                            "The reason of the report was empty, please try choice again."
                        )
                    }
                    ReportViewModel.INVALID_FORMAT_MESSAGE_EMPTY -> {
                        activity.showToast(
                            "The message of the report was empty, please try key-in again."
                        )
                    }
                    ReportViewModel.NO_ONE_KNOWS -> {
                        Logger.i("Unknown invalidReport value NO_ONE_KNOWS = $it")
                    }
                }
            }
        }

        return binding.root
    }

    override fun dismiss() {
        binding.layoutReport.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.anim_slide_down)
        )
        lifecycleScope.launch {
            delay(200)
            super.dismiss()
            viewModel.onLeaveCompleted()
        }
    }
}
