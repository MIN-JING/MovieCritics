package com.jim.moviecritics.watchlist

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.view.*
import androidx.compose.ui.res.integerArrayResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.FragmentWatchlistBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.Logger
import java.util.*

class WatchlistFragment : Fragment() {

    private val viewModel by viewModels<WatchlistViewModel> { getVmFactory(WatchlistFragmentArgs.fromBundle(requireArguments()).userKey) }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu, menu)
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
        binding.viewModel = viewModel


        val intent = Intent(Intent.ACTION_INSERT)
        intent.data = CalendarContract.CONTENT_URI


        binding.recyclerWatchlist.adapter = WatchlistAdapter(
            WatchlistAdapter.OnClickListener {
                Logger.i("WatchlistAdapter.OnClickListener it = $it")

                context?.let { context -> viewModel.showDateTimeDialog(context) }
                intent.putExtra(CalendarContract.Events.TITLE, it.title)
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "AppWorks Cinema")
                intent.putExtra(CalendarContract.Events.DESCRIPTION, it.overview)
                intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
//                intent.putExtra(CalendarContract.Events.DTSTART, )
            }
        )

        viewModel.user.observe(viewLifecycleOwner) {
            Logger.i("Watchlist ViewModel.user = $it")
        }

        viewModel.liveWatchListByUser.observe(viewLifecycleOwner) {
            Logger.i("Watchlist ViewModel.liveWatchListByUser = $it")
            if (it != null) {
                val list = mutableListOf<String>()
                for (value in it) {
                    list.add(value.imdbID)
                }
                viewModel.getWatchListFull(list)
            }
        }

        viewModel.timeStamp.observe(viewLifecycleOwner) {
            Logger.i("Watchlist ViewModel.timeStamp = $it")
        }

        return binding.root
    }
}