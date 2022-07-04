package com.jim.moviecritics.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jim.moviecritics.data.Look
import com.jim.moviecritics.data.LookItem
import com.jim.moviecritics.databinding.*



class SearchAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<LookItem, RecyclerView.ViewHolder>(DiffCallback) {

    class LookMovieViewHolder(private var binding: ItemSearchMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(look: Look, onClickListener: OnClickListener) {
            binding.look = look
            binding.root.setOnClickListener { onClickListener.onClick(look) }
            binding.executePendingBindings()
        }
    }

    class LookTelevisionViewHolder(private var binding: ItemSearchTelevisionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(look: Look, onClickListener: OnClickListener) {
            binding.look = look
            binding.root.setOnClickListener { onClickListener.onClick(look) }
            binding.executePendingBindings()
        }
    }

    class LookPersonViewHolder(private var binding: ItemSearchPersonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(look: Look, onClickListener: OnClickListener) {
            binding.look = look
//            binding.knownFor = knownFor
            binding.root.setOnClickListener { onClickListener.onClick(look) }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<LookItem>() {
        override fun areItemsTheSame(oldItem: LookItem, newItem: LookItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: LookItem, newItem: LookItem): Boolean {
            return oldItem.id == newItem.id
        }

        private const val ITEM_VIEW_TYPE_LOOK_MOVIE = 0x00
        private const val ITEM_VIEW_TYPE_LOOK_TELEVISION = 0x01
        private const val ITEM_VIEW_TYPE_LOOK_PERSON = 0x02
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_LOOK_MOVIE -> LookMovieViewHolder(
                ItemSearchMovieBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            ITEM_VIEW_TYPE_LOOK_TELEVISION -> LookTelevisionViewHolder(
                ItemSearchTelevisionBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            ITEM_VIEW_TYPE_LOOK_PERSON -> LookPersonViewHolder(
                ItemSearchPersonBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LookMovieViewHolder -> {
                holder.bind((getItem(position) as LookItem.LookMovie).look, onClickListener)
            }
            is LookTelevisionViewHolder -> {
                holder.bind((getItem(position) as LookItem.LookTelevision).look, onClickListener)
            }
            is LookPersonViewHolder -> {
                    holder.bind(
                        (getItem(position) as LookItem.LookPerson).look,
//                        (getItem(position) as LookItem.LookPerson).knownFor,
                        onClickListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is LookItem.LookMovie -> ITEM_VIEW_TYPE_LOOK_MOVIE
            is LookItem.LookTelevision -> ITEM_VIEW_TYPE_LOOK_TELEVISION
            is LookItem.LookPerson -> ITEM_VIEW_TYPE_LOOK_PERSON
        }
    }

    class OnClickListener(val clickListener: (look: Look) -> Unit) {
        fun onClick(look: Look) = clickListener(look)
    }
}