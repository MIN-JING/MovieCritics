package com.jim.moviecritics.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jim.moviecritics.data.Cast
import com.jim.moviecritics.databinding.ItemDetailCastBinding


class CastAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Cast, CastAdapter.CastViewHolder>(DiffCallback) {

    class CastViewHolder(private var binding: ItemDetailCastBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cast: Cast, onClickListener: OnClickListener) {
            binding.cast = cast
            binding.root.setOnClickListener { onClickListener.onClick(cast) }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Cast>() {
        override fun areItemsTheSame(oldItem: Cast, newItem: Cast): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Cast, newItem: Cast): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        return CastViewHolder(
            ItemDetailCastBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.bind((getItem(position)), onClickListener)
    }

    class OnClickListener(val clickListener: (cast: Cast) -> Unit) {
        fun onClick(cast: Cast) = clickListener(cast)
    }
}