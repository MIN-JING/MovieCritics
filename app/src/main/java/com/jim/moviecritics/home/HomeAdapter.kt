package com.jim.moviecritics.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jim.moviecritics.data.HomeItem
import com.jim.moviecritics.data.Trend
import com.jim.moviecritics.databinding.ItemHomePopularBinding


class HomeAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<HomeItem, RecyclerView.ViewHolder>(DiffCallback) {

    class PopularMovieViewHolder(private var binding: ItemHomePopularBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(trend: Trend, onClickListener: OnClickListener) {
                binding.trend = trend
                binding.root.setOnClickListener { onClickListener.onClick(trend) }
                binding.executePendingBindings()
            }
        }

    companion object DiffCallback : DiffUtil.ItemCallback<HomeItem>() {
        override fun areItemsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
            return oldItem.id == newItem.id
        }

        private const val ITEM_VIEW_TYPE_MOVIE_POPULAR = 0x00
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_MOVIE_POPULAR -> PopularMovieViewHolder(
                ItemHomePopularBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PopularMovieViewHolder -> {
                holder.bind((getItem(position) as HomeItem.PopularMovie).trend, onClickListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HomeItem.PopularMovie -> ITEM_VIEW_TYPE_MOVIE_POPULAR
        }
    }

    class OnClickListener(val clickListener: (trend: Trend) -> Unit) {
        fun onClick(trend: Trend) = clickListener(trend)
    }
}