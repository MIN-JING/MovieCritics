package com.jim.moviecritics.downshift

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.FragmentDownshiftBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.home.HomeViewModel

class DownshiftFragment : Fragment() {

    private val viewModel by viewModels<DownshiftViewModel> { getVmFactory() }

//    companion object {
//        fun newInstance() = DownshiftFragment()
//    }
//
//    private lateinit var viewModel: DownshiftViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val binding = FragmentDownshiftBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
//        return inflater.inflate(R.layout.fragment_downshift, container, false)
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(DownshiftViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}