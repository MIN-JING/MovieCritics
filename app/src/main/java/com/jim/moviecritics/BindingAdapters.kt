package com.jim.moviecritics

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.jim.moviecritics.data.HomeItem
import com.jim.moviecritics.home.HomeAdapter
import com.jim.moviecritics.util.Logger


@BindingAdapter("homeItems")
fun bindRecyclerViewWithHomeItems(recyclerView: RecyclerView, homeItems: List<HomeItem>?) {
    homeItems?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is HomeAdapter -> { submitList(it)
                    Logger.i("bindRecyclerViewWithHomeItems = $it")
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

/**
 * Displays currency price to [TextView] by [Double]
 */
//@BindingAdapter("average")
//fun bindAverage(textView: TextView, average: Double?) {
//    average?.let { textView.text = MovieApplication.instance.getString(it.toInt()) }
//}