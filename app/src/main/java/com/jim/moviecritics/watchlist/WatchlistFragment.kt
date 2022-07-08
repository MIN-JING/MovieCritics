package com.jim.moviecritics.watchlist

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.Time
import android.view.*
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.FragmentWatchlistBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.Logger
import java.text.SimpleDateFormat
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

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerOnDataSetListener =
            TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)

            }

        binding.recyclerWatchlist.adapter = WatchlistAdapter(
            WatchlistAdapter.OnClickListener {
                Logger.i("WatchlistAdapter.OnClickListener it = $it")
                TimePickerDialog(context,
                    { _, hour, minute ->
                        binding.textView.text = "現在時間是 $hour : $minute" },
                    hour, minute, true).show()
//                binding.textView.text = "現在時間是" + hour
            }
        )

        viewModel.user.observe(viewLifecycleOwner) {
            Logger.i("Watchlist ViewModel.user = $it")
        }

        return binding.root
    }
}