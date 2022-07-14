package com.jim.moviecritics


import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.jim.moviecritics.data.*
import com.jim.moviecritics.detail.CastAdapter
import com.jim.moviecritics.detail.DetailViewModel
import com.jim.moviecritics.detail.ReviewAdapter
import com.jim.moviecritics.home.HomeAdapter
import com.jim.moviecritics.profile.FavoriteItemAdapter
import com.jim.moviecritics.profile.GuideItemReviewAdapter
import com.jim.moviecritics.search.SearchAdapter
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.watchlist.WatchlistAdapter


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

@BindingAdapter("comments")
fun bindRecyclerViewWithComments(recyclerView: RecyclerView, comments: List<Comment>?) {
    comments?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is ReviewAdapter -> {
                    submitList(it)
                    Logger.i("is ReviewAdapter bindRecyclerViewWithComments = $it")
                }
                is GuideItemReviewAdapter -> {
                    submitList(it)
                    Logger.i("is GuideItemReviewAdapter bindRecyclerViewWithComments = $it")
                }
            }
        }
    }
}

//@BindingAdapter("reviews", "viewModel")
//fun bindDetailCommentsRecyclerView(
//    recyclerView: RecyclerView,
//    comments: List<Comment>?,
//    viewModel: DetailViewModel,
//    onClickListener: ReviewAdapter.OnClickListener
//) {
//    comments?.let {
//        recyclerView.adapter = ReviewAdapter(onClickListener, viewModel).apply {
//            submitList(it)
//        }
//    }
//}



@BindingAdapter("finds")
fun bindRecyclerViewWithFinds(recyclerView: RecyclerView, finds: List<Find>?) {
    finds?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is FavoriteItemAdapter -> {
                    submitList(it)
                    Logger.i("is FavoriteItemAdapter bindRecyclerViewWithFinds = $it")
                }
                is WatchlistAdapter -> {
                    submitList(it)
                    Logger.i("is WatchlistAdapter bindRecyclerViewWithFinds = $it")
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

@BindingAdapter("imageUrlWithCircleCrop")
fun bindImageWithCircleCrop(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().build()
        GlideApp.with(imgView.context)
            .load(imgUri)
            .circleCrop()
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
            )
            .into(imgView)
    }
}

@BindingAdapter("app:tint")
fun ImageView.setImageTint(@ColorInt color: Int) {
    setColorFilter(color)
}

//@BindingAdapter("imgRes")
//fun setImageResource(imageView: ImageView, resource: Int) {
//    imageView.setImageResource(resource)
//}

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

///**
// * Displays currency price to [TextView] by [Double]
// */
//@BindingAdapter("average")
//fun bindAverage(textView: TextView, average: Double?) {
//    average?.let { textView.text = MovieApplication.instance.getString(it.toInt()) }
//}