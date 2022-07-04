package com.jim.moviecritics.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.ItemProfileGuideBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.Logger


class ItemGuideFragment : Fragment() {

    private val viewModel by viewModels<ProfileViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val binding = ItemProfileGuideBinding.inflate(inflater, container, false)
//        binding.lifecycleOwner = viewLifecycleOwner
//        binding.viewModel = viewModel

        binding.recyclerProfileGuide.adapter = GuideItemReviewAdapter(
            GuideItemReviewAdapter.OnClickListener {
                Logger.i("GuideItemReviewAdapter.OnClickListener = $it")
            }
        )

        return binding.root
    }


}