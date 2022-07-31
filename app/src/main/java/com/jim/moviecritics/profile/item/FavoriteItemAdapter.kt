package com.jim.moviecritics.profile.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jim.moviecritics.data.Find
import com.jim.moviecritics.databinding.ItemProfileFavoriteChildBinding

class FavoriteItemAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Find, FavoriteItemAdapter.FavoriteItemViewHolder>(DiffCallback) {

    class FavoriteItemViewHolder(private var binding: ItemProfileFavoriteChildBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(find: Find, onClickListener: OnClickListener) {
            binding.find = find
            binding.root.setOnClickListener { onClickListener.onClick(find) }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Find>() {
        override fun areItemsTheSame(oldItem: Find, newItem: Find): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Find, newItem: Find): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteItemViewHolder {
        return FavoriteItemViewHolder(
            ItemProfileFavoriteChildBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FavoriteItemViewHolder, position: Int) {
        holder.bind((getItem(position)), onClickListener)
    }

    class OnClickListener(val clickListener: (find: Find) -> Unit) {
        fun onClick(find: Find) = clickListener(find)
    }
}
