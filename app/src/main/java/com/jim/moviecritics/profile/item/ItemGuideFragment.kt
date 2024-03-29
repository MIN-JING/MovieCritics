package com.jim.moviecritics.profile.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jim.moviecritics.databinding.ItemProfileGuideBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.Logger

class ItemGuideFragment : Fragment() {

    private val viewModel by viewModels<ItemGuideViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val binding = ItemProfileGuideBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val guideItemReviewAdapter = GuideItemReviewAdapter(
            GuideItemReviewAdapter.OnClickListener {
                Logger.i("GuideItemReviewAdapter.OnClickListener it = $it")
            },
            viewModel
        )

        binding.recyclerProfileGuideReview.adapter = guideItemReviewAdapter

        viewModel.livePersonalComments.observe(viewLifecycleOwner) { personalComments ->
            Logger.i("GuideItemReview ViewModel.livePersonalComments = $personalComments")
            personalComments?.let { comments ->
                val list = mutableListOf<String>()
                comments.forEach { list.add(it.imdbID) }
                viewModel.getFindsByImdbIDs(list)
            }
            viewModel.isMovieMapReady.observe(viewLifecycleOwner) { boolean ->
                Logger.i("GuideItemReview ViewModel.isMovieMapReady = $boolean")
                guideItemReviewAdapter.submitList(personalComments)
            }
        }

        return binding.root
    }
}
