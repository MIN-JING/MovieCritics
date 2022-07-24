package com.jim.moviecritics.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jim.moviecritics.data.Watch
import com.jim.moviecritics.databinding.ItemWatchlistBinding

class WatchlistAdapter(
    private val onClickListener: OnClickListener,
    val viewModel: WatchlistViewModel
) :
    ListAdapter<Watch, WatchlistAdapter.WatchlistItemViewHolder>(DiffCallback) {

    class WatchlistItemViewHolder(
        private var binding: ItemWatchlistBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(watch: Watch, onClickListener: OnClickListener, viewModel: WatchlistViewModel) {
            binding.watch = watch
            binding.viewModel = viewModel

            binding.layoutWatchlistItemCalendar.setOnClickListener {
                onClickListener.onClick(watch)
            }

            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Watch>() {
        override fun areItemsTheSame(oldItem: Watch, newItem: Watch): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Watch, newItem: Watch): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistItemViewHolder {
        return WatchlistItemViewHolder(
            ItemWatchlistBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: WatchlistItemViewHolder, position: Int) {
        holder.bind((getItem(position)), onClickListener, viewModel)
    }

    class OnClickListener(val clickListener: (watch: Watch) -> Unit) {
        fun onClick(watch: Watch) = clickListener(watch)
    }
}
