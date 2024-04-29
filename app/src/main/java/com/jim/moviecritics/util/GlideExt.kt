package com.jim.moviecritics.util

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jim.moviecritics.R

@Composable
fun GlideImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeHolder: Int = R.drawable.ic_movie,
    error: Int = R.drawable.ic_error
) {
    val context = LocalContext.current
    val imageState = remember { mutableStateOf<BitmapPainter?>(null) }

    DisposableEffect(key1 = imageUrl) {
        val glide = Glide.with(context)
        val target = object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                imageState.value = BitmapPainter(resource.toBitmap().asImageBitmap())
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        }

        glide
            .load(imageUrl)
            .apply(RequestOptions().placeholder(placeHolder).error(error))
            .into(target)

        onDispose {
            glide.clear(target)
        }
    }

    val painter = imageState.value

    if (painter != null) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        Image(
            painter = painterResource(id = placeHolder),
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}