package com.jim.moviecritics.search

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.FragmentSearchBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.ext.showToast
import com.jim.moviecritics.home.HomeViewModel
import com.jim.moviecritics.pending.PendingViewModel
import com.jim.moviecritics.search.SearchViewModel.Companion.INVALID_FORMAT_SEARCH_KEY_EMPTY
import com.jim.moviecritics.util.Logger

class SearchFragment : Fragment() {

    private val viewModel by viewModels<SearchViewModel> { getVmFactory() }


//    companion object {
//        fun newInstance() = SearchFragment()
//    }
//
//    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.lookItems.observe(viewLifecycleOwner, Observer {
            Logger.i("viewModel.lookItems = $it")
        })

        binding.recyclerviewSearch.adapter = SearchAdapter(
            SearchAdapter.OnClickListener {
                Logger.i("SearchAdapter.OnClickListener it = $it")
                Logger.i("SearchAdapter.OnClickListener it.id = ${it.id}")
            }
        )
        
//        binding.recyclerviewSearch.adapter = ExpandAdapter(
//            onSectionClickListener = { lookItem ->
//
//            }
//        )

        viewModel.invalidSearch.observe(viewLifecycleOwner, Observer {
            Logger.i("viewModel.invalidSearch.value = ${viewModel.invalidSearch.value}")
            it?.let {
                when (it) {
                    INVALID_FORMAT_SEARCH_KEY_EMPTY -> {
                        activity.showToast("Please input search key text")
                    }
                    else -> { Logger.i("Unknown invalidSearch value = $it") }
                }
            }
        })

        viewModel.searchKey.observe(viewLifecycleOwner, Observer {
            Logger.i("viewModel.searchKey.value = ${viewModel.searchKey.value}")
        })

        return binding.root
//        return inflater.inflate(R.layout.fragment_search, container, false)
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}