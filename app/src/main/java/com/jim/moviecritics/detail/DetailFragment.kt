package com.jim.moviecritics.detail


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.jim.moviecritics.NavigationDirections
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.databinding.FragmentDetailBinding
import com.jim.moviecritics.util.Logger

class DetailFragment : Fragment() {

    private val viewModel by viewModels<DetailViewModel> { getVmFactory(DetailFragmentArgs.fromBundle(requireArguments()).movie) }

//    companion object {
//        fun newInstance() = DetailFragment()
//    }
//
//    private lateinit var viewModel: DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val binding = FragmentDetailBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel


        viewModel.movie.observe(viewLifecycleOwner, Observer {
            Logger.i("DetailViewModel.movie = $it")
        })

        viewModel.navigateToPending.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.i("DetailViewModel.navigateToPending = $it")
                findNavController().navigate(NavigationDirections.navigateToPendingFragment(it))
                viewModel.onPendingNavigated()
            }
        })

        viewModel.leaveDetail.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.i(" DetailViewModel.leaveDetail =  $it")
                if (it) findNavController().popBackStack()
            }
        })

        return binding.root

//        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}