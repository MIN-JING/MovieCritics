package com.jim.moviecritics


import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.jim.moviecritics.data.*
import com.jim.moviecritics.detail.CastAdapter
import com.jim.moviecritics.home.HomeAdapter
import com.jim.moviecritics.profile.FavoriteItemAdapter
import com.jim.moviecritics.search.SearchAdapter
import com.jim.moviecritics.util.Logger


@BindingAdapter("homeItems")
fun bindRecyclerViewWithHomeItems(recyclerView: RecyclerView, homeItems: List<HomeItem>?) {
    homeItems?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is HomeAdapter -> {
                    submitList(it)
                    Logger.i("bindRecyclerViewWithHomeItems = $it")
                }
            }
        }
    }
}

@BindingAdapter("lookItems")
fun bindRecyclerViewWithLookItems(recyclerView: RecyclerView, lookItems: List<LookItem>?) {
    lookItems?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is SearchAdapter -> {
                    submitList(it)
                    Logger.i("bindRecyclerViewWithLookItems = $it")
                }
            }
        }
    }
}

@BindingAdapter("casts")
fun bindRecyclerViewWithCasts(recyclerView: RecyclerView, casts: List<Cast>?) {
    casts?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is CastAdapter -> {
                    submitList(it)
                    Logger.i("bindRecyclerViewWithCasts = $it")
                }
            }
        }
    }
}

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
        GlideApp.with(imgView.context)
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
        GlideApp.with(imgView.context)
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