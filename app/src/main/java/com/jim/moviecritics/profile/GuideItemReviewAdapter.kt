package com.jim.moviecritics.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jim.moviecritics.data.Comment
import com.jim.moviecritics.databinding.ItemProfileGuideCommentBinding


class GuideItemReviewAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Comment, GuideItemReviewAdapter.GuideItemReviewViewHolder>(DiffCallback) {

    class GuideItemReviewViewHolder(private var binding: ItemProfileGuideCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: Comment, onClickListener: OnClickListener) {
            binding.comment = comment
            binding.root.setOnClickListener { onClickListener.onClick(comment) }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideItemReviewViewHolder {
        return GuideItemReviewViewHolder(
            ItemProfileGuideCommentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: GuideItemReviewViewHolder, position: Int) {
        holder.bind((getItem(position)), onClickListener)
    }

    class OnClickListener(val clickListener: (comment: Comment) -> Unit) {
        fun onClick(comment: Comment) = clickListener(comment)
    }
}