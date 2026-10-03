package com.jim.moviecritics.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jim.moviecritics.NavigationDirections
import com.jim.moviecritics.databinding.FragmentSearchBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.ext.showToast
import com.jim.moviecritics.ui.theme.MovieCriticsTheme

class SearchFragment : Fragment() {

    private val viewModel by viewModels<SearchViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.composeViewSearch.setContent {
            MovieCriticsTheme {
                SearchScreen(
                    modifier = Modifier,
                    viewModel = viewModel,
                )
            }
        }

        viewModel.navigateToDetail.observe(viewLifecycleOwner) { movie ->
            movie?.let {
                findNavController().navigate(NavigationDirections.navigateToDetailFragment(it))
                viewModel.onDetailNavigated()
            }
        }

        viewModel.userMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                activity.showToast(it)
                viewModel.onUserMessageShown()
            }
        }

        return binding.root
    }
}
