package com.jim.moviecritics.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jim.moviecritics.data.LookItem
import com.jim.moviecritics.databinding.ItemSearchSectionBinding
import com.jim.moviecritics.databinding.ItemSearchSectionItemMovieBinding
import com.jim.moviecritics.ext.setOnSingleClickListener

class ExpandAdapter(
    private val onSectionClickListener: (LookItem) -> Unit
) :
    ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback) {

    class SectionViewHolder(
        private var binding: ItemSearchSectionBinding,
        onSectionClickListener: (LookItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnSingleClickListener {
                binding.lookItem?.let {
                    onSectionClickListener(it)
                }
            }
        }
        fun bind(lookItem: LookItem) {
            binding.lookItem = lookItem
            binding.executePendingBindings()
        }
    }

    class SectionItemViewHolder(private var binding: ItemSearchSectionItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(lookItemMovie: LookItem.LookMovie) {
            binding.lookItemMovie = lookItemMovie
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is LookItem && newItem is LookItem -> {
                    oldItem === newItem
                }
                oldItem is LookItem.LookMovie && newItem is LookItem.LookMovie -> {
                    oldItem.look === newItem.look
                }
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is LookItem && newItem is LookItem -> {
                    oldItem.id == newItem.id
                }
                oldItem is LookItem.LookMovie && newItem is LookItem.LookMovie -> {
                    oldItem.look.id == newItem.look.id
                }
                else -> false
            }
        }

        private const val VIEW_TYPE_SECTION = 0x01
        private const val VIEW_TYPE_SECTION_ITEM = 0x02
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SECTION -> SectionViewHolder(
                ItemSearchSectionBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                onSectionClickListener
            )
            VIEW_TYPE_SECTION_ITEM -> SectionItemViewHolder(
                ItemSearchSectionItemMovieBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw(Throwable("View type not matching"))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is LookItem -> (holder as? SectionViewHolder)?.bind(item)
            is LookItem.LookMovie -> (holder as? SectionItemViewHolder)?.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is LookItem -> VIEW_TYPE_SECTION
            is LookItem.LookMovie -> VIEW_TYPE_SECTION_ITEM
            else -> super.getItemViewType(position)
        }
    }
}
