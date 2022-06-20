package com.jim.moviecritics.review


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.jim.moviecritics.databinding.FragmentReviewBinding
import com.jim.moviecritics.detail.DetailFragmentArgs
import com.jim.moviecritics.ext.getVmFactory

class ReviewFragment : Fragment() {

    private val viewModel by viewModels<ReviewViewModel> { getVmFactory(DetailFragmentArgs.fromBundle(requireArguments()).movie) }

//    companion object {
//        fun newInstance() = ReviewFragment()
//    }
//
//    private lateinit var viewModel: ReviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val binding = FragmentReviewBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
//        return inflater.inflate(R.layout.fragment_review, container, false)
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(ReviewViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}