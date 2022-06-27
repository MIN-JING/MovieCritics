package com.jim.moviecritics

import android.graphics.drawable.LayerDrawable
import android.widget.ImageView
import android.widget.RatingBar
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.jim.moviecritics.data.Cast
import com.jim.moviecritics.data.HomeItem
import com.jim.moviecritics.data.LookItem
import com.jim.moviecritics.detail.CastAdapter
import com.jim.moviecritics.home.HomeAdapter
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
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
            )
            .into(imgView)
    }
}

//@BindingAdapter("ratingValue")
//fun bindRating(ratingBar: RatingBar, rating: Float?) {
//    rating?.let {
//        ratingBar.rating = rating
////        val stars = ratingBar.progressDrawable as LayerDrawable
////        stars.getDrawable(2).colorFilter
//        val roundVal = Math.round(rating)
//        ratingBar.numStars = roundVal
//    }
//}
//
//@BindingAdapter("ratingFloat")
//fun setRating(view: RatingBar, rating: String?) {
//    val rate = rating?.toFloat()
//    if (rate != null) {
//        view.rating = rate
//    }
//}

/**
 * Displays currency price to [TextView] by [Double]
 */
//@BindingAdapter("average")
//fun bindAverage(textView: TextView, average: Double?) {
//    average?.let { textView.text = MovieApplication.instance.getString(it.toInt()) }
//}