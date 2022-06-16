package com.jim.moviecritics.search

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.FragmentSearchBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.home.HomeViewModel

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

        return binding.root
//        return inflater.inflate(R.layout.fragment_search, container, false)
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}