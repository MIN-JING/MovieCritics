package com.jim.moviecritics.watchlist


import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.FragmentWatchlistBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.Logger



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
//                throw RuntimeException("Test Crash") // Force a crash
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
//        intent.data = CalendarContract.CONTENT_URI
        intent.data = CalendarContract.Events.CONTENT_URI


        binding.recyclerWatchlist.adapter = WatchlistAdapter(
            WatchlistAdapter.OnClickListener {
                Logger.i("WatchlistAdapter.OnClickListener it = $it")

                context?.let { context -> viewModel.showDateTimeDialog(context) }
//                intent.putExtra(CalendarContract.Events.CALENDAR_ID, 1)
                intent.putExtra(CalendarContract.Events.TITLE, "[Movie] ${it.title}")
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "AppWorks Cinema")
                intent.putExtra(CalendarContract.Events.DESCRIPTION, it.overview)
                intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
            }
        )

        viewModel.user.observe(viewLifecycleOwner) {
            Logger.i("Watchlist ViewModel.user = $it")
        }

        viewModel.liveWatchListByUser.observe(viewLifecycleOwner) {
            Logger.i("Watchlist ViewModel.liveWatchListByUser = $it")
            it?.let {
                val list = mutableListOf<String>()
                for (value in it) {
                    list.add(value.imdbID)
                }
                viewModel.getWatchListFull(list)
            }
        }

        viewModel.timeStamp.observe(viewLifecycleOwner) {
            Logger.i("Watchlist ViewModel.timeStamp = $it")
//            intent.putExtra(CalendarContract.Events.DTSTART, it.seconds * 1000)
//            intent.putExtra(CalendarContract.Events.DTSTART, 1660959000000)
//            intent.putExtra(CalendarContract.Events.DTEND, it.seconds + 2.5 * 3600L)
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, it.seconds * 1000L)
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, it.seconds * 1000L + 9000000L)
//            intent.putExtra(CalendarContract.Events.EVENT_COLOR_KEY, )
//            intent.putExtra(CalendarContract.Events.HAS_ALARM, 1)

            context?.let { context ->
                if (intent.resolveActivity(context.packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(context, "There is no app that can support this action", Toast.LENGTH_LONG).show()
                }
            }


        }

        return binding.root
    }
}