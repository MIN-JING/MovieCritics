package com.jim.moviecritics.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jim.moviecritics.data.Comment
import com.jim.moviecritics.databinding.ItemDetailReviewBinding
import com.jim.moviecritics.util.Logger

class ReviewAdapter(
    private val onClickListener: OnClickListener,
    val viewModel: DetailViewModel
) : ListAdapter<Comment, ReviewAdapter.ReviewViewHolder>(DiffCallback) {

    class ReviewViewHolder(
        private val binding: ItemDetailReviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: Comment, onClickListener: OnClickListener, viewModel: DetailViewModel) {
            binding.comment = comment
            binding.viewModel = viewModel
            binding.root.setOnClickListener { onClickListener.onClick(comment) }
            binding.executePendingBindings()
            Logger.i("bind comment = $comment")
            Logger.i("bind comment.userID = ${comment.userID}")
            Logger.i("comment userMap = ${viewModel.usersMap[comment.userID]}")
            Logger.i("comment userName = ${viewModel.usersMap[comment.userID]?.name}")
            Logger.i("comment userPic = ${viewModel.usersMap[comment.userID]?.pictureUri}")
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(
            ItemDetailReviewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind((getItem(position)), onClickListener, viewModel)
    }

    class OnClickListener(val clickListener: (comment: Comment) -> Unit) {
        fun onClick(comment: Comment) = clickListener(comment)
    }
}
