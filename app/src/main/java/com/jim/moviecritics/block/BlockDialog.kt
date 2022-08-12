package com.jim.moviecritics.block

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
import com.jim.moviecritics.databinding.DialogBlockBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.follow.FollowDialogArgs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BlockDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<BlockViewModel> {
        getVmFactory(FollowDialogArgs.fromBundle(requireArguments()).userKey)
    }
    private lateinit var binding: DialogBlockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ***** Let layout showing match constraint *****
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BlockDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DialogBlockBinding.inflate(inflater, container, false)
        binding.layoutBlock.startAnimation(
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

        return binding.root
    }

    override fun dismiss() {
        binding.layoutBlock.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.anim_slide_down)
        )
        lifecycleScope.launch {
            delay(200)
            super.dismiss()
            viewModel.onLeaveCompleted()
        }
    }
}
