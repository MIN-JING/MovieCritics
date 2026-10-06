package com.jim.moviecritics

import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jim.moviecritics.data.*
import com.jim.moviecritics.profile.item.FavoriteItemAdapter
import com.jim.moviecritics.util.Logger

@BindingAdapter("finds")
fun bindRecyclerViewWithFinds(recyclerView: RecyclerView, finds: List<Find>?) {
    finds?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is FavoriteItemAdapter -> {
                    submitList(it)
                    Logger.i("is FavoriteItemAdapter bindRecyclerViewWithFinds = $it")
                }
            }
        }
    }
}

/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_movie)
                    .error(R.drawable.ic_error)
            )
            .into(imgView)
    }
}

@BindingAdapter("imageUrlWithCircleCrop")
fun bindImageWithCircleCrop(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().build()
        Glide.with(imgView.context)
            .load(imgUri)
            .circleCrop()
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_movie)
                    .error(R.drawable.ic_error)
            )
            .into(imgView)
    }
}

@BindingAdapter("tint")
fun ImageView.setImageTint(@ColorInt color: Int) {
    setColorFilter(color)
}
