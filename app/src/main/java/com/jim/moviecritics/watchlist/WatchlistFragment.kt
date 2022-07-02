package com.jim.moviecritics.watchlist

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.FragmentWatchlistBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.Logger

class WatchlistFragment : Fragment() {

    private val viewModel by viewModels<WatchlistViewModel> { getVmFactory(WatchlistFragmentArgs.fromBundle(requireArguments()).userKey) }

//    companion object {
//        fun newInstance() = DownshiftFragment()
//    }
//
//    private lateinit var viewModel: DownshiftViewModel

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
//    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu, menu)
//        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_button_test -> {
                Logger.i("toolbar_button_test onClick")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        setHasOptionsMenu(true)

        val binding = FragmentWatchlistBinding.inflate(inflater, container, false)
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