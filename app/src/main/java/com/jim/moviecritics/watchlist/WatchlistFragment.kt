package com.jim.moviecritics.watchlist

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jim.moviecritics.data.Watch
import com.jim.moviecritics.databinding.FragmentWatchlistBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.Logger
import java.util.concurrent.TimeUnit

class WatchlistFragment : Fragment() {

    private val viewModel by viewModels<WatchlistViewModel> {
        getVmFactory(WatchlistFragmentArgs.fromBundle(requireArguments()).userKey)
    }

    private var watch = Watch()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        setHasOptionsMenu(true)

        val binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val intent = Intent(Intent.ACTION_INSERT)
        intent.data = CalendarContract.Events.CONTENT_URI

        val watchlistAdapter = WatchlistAdapter(
            WatchlistAdapter.OnClickListener {
                Logger.i("WatchlistAdapter.OnClickListener it = $it")
                watch = it
                context?.let { context -> viewModel.showDateTimeDialog(context) }
                intent.putExtra(
                    CalendarContract.Events.TITLE,
                    "[Movie] ${viewModel.movieMap[it.imdbID]?.title}"
                )
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "AppWorks Cinema")
                intent.putExtra(
                    CalendarContract.Events.DESCRIPTION,
                    viewModel.movieMap[it.imdbID]?.overview
                )
                intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
            },
            viewModel
        )

        binding.recyclerWatchlist.adapter = watchlistAdapter

        viewModel.liveWatchListByUser.observe(viewLifecycleOwner) { watchListByUser ->
            Logger.i("Watchlist ViewModel.liveWatchListByUser = $watchListByUser")
            watchListByUser?.let { watchList ->
                val list = mutableListOf<String>()
                watchList.forEach { list.add(it.imdbID) }
                viewModel.getFindsByImdbIDs(list)
            }

            viewModel.isMovieMapReady.observe(viewLifecycleOwner) { boolean ->
                Logger.i("Watchlist ViewModel.isMovieMapReady = $boolean")
                watchlistAdapter.submitList(watchListByUser)
            }
        }

        viewModel.timeStamp.observe(viewLifecycleOwner) {
            Logger.i("Watchlist ViewModel.timeStamp = $it")
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, it.seconds * 1000L)
            intent.putExtra(
                CalendarContract.EXTRA_EVENT_END_TIME,
                it.seconds * 1000L + 9000000L
            )
            watch.expiration = it
            viewModel.pushSingleWatchListExpiration(watch)

            viewModel.movieMap[watch.imdbID]?.let { find ->
                context?.let { context ->
                    viewModel.scheduleReminder(3, TimeUnit.SECONDS, find.title, context)
                }
            }

            context?.let { context ->
                if (intent.resolveActivity(context.packageManager) != null) {
                    startActivity(intent)
                    Logger.i("pushSingleWatchListExpiration(watch) = $watch")
                } else {
                    Toast.makeText(
                        context, "There is no app that can support this action",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        viewModel.user.observe(viewLifecycleOwner) {
            Logger.i("Watchlist ViewModel.user = $it")
        }

        return binding.root
    }
}
