package com.jim.moviecritics.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jim.moviecritics.NavigationDirections
import com.jim.moviecritics.databinding.FragmentHomeBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.Logger

class HomeFragment : Fragment() {

    private val viewModel by viewModels<HomeViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.homeItems.observe(viewLifecycleOwner) {
            Logger.i("HomeViewModel.homeItems = $it")
        }

        binding.recyclerviewHomePopular.adapter = HomeAdapter(
            HomeAdapter.OnClickListener {
                Logger.i("HomeAdapter.OnClickListener it = $it")
                viewModel.getMovieFull(it.id)
            }
        )

        viewModel.navigateToDetail.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToDetailFragment(it))
                viewModel.onDetailNavigated()
            }
        }
        return binding.root
    }
}
