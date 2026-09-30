package com.jim.moviecritics.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jim.moviecritics.NavigationDirections
import com.jim.moviecritics.databinding.FragmentSearchBinding
import com.jim.moviecritics.ext.getVmFactory

class SearchFragment : Fragment() {

    private val viewModel by viewModels<SearchViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.composeViewSearch.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    SearchScreen(
                        viewModel = viewModel,
                        onNavigateToDetail = { movie ->
                            findNavController().navigate(
                                NavigationDirections.navigateToDetailFragment(movie)
                            )
                        }
                    )
                }
            }
        }
        return binding.root
    }
}
