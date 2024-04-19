package com.jim.moviecritics.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jim.moviecritics.databinding.FragmentSearchBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.ext.showToast
import com.jim.moviecritics.search.SearchViewModel.Companion.INVALID_FORMAT_SEARCH_KEY_EMPTY
import com.jim.moviecritics.util.Logger

class SearchFragment : Fragment() {

    private val viewModel by viewModels<SearchViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.composeViewSearch.setContent {
            MaterialTheme {
                SearchScreen(
                    modifier = Modifier,
                    viewModel = viewModel,
                    navigateToDetail = { Logger.i("navigateToDetail it = $it") }
                )
            }
        }
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

//        binding.recyclerviewSearch.adapter = SearchAdapter(
//            SearchAdapter.OnClickListener {
//                Logger.i("SearchAdapter.OnClickListener it = $it")
//                Logger.i("SearchAdapter.OnClickListener it.id = ${it.id}")
//            }
//        )

        viewModel.invalidSearch.observe(viewLifecycleOwner) {
            Logger.i("viewModel.invalidSearch.value = ${viewModel.invalidSearch.value}")
            it?.let {
                when (it) {
                    INVALID_FORMAT_SEARCH_KEY_EMPTY -> {
                        activity.showToast("Please input search key text")
                    }
                    else -> { Logger.i("Unknown invalidSearch value = $it") }
                }
            }
        }

        viewModel.searchKey.observe(viewLifecycleOwner) {
            Logger.i("viewModel.searchKey.value = ${viewModel.searchKey.value}")
        }

        return binding.root
    }
}