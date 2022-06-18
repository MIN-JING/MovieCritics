package com.jim.moviecritics.home


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.databinding.FragmentHomeBinding
import com.jim.moviecritics.util.Logger


class HomeFragment : Fragment() {

    private val viewModel by viewModels<HomeViewModel> { getVmFactory() }

//    companion object {
//        fun newInstance() = HomeFragment()
//    }
//
//    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.homeItems.observe(viewLifecycleOwner, Observer {
            Logger.i("viewModel.homeItems = $it")

        })

        binding.recyclerviewPopular.adapter = HomeAdapter(
            HomeAdapter.OnClickListener {
                Logger.i("HomeAdapter.OnClickListener it = $it")
                viewModel.navigateToDetail(it)
            }
        )

        viewModel.testComments.observe(viewLifecycleOwner, Observer {
            Logger.i("viewModel.testComments = $it")
        })

        return binding.root
//        return inflater.inflate(R.layout.home_fragment, container, false)
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}