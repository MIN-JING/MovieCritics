package com.jim.moviecritics.util

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

enum class InsetMode {
    MARGIN,
    PADDING
}

/**
 * Applies system bar insets as margins or padding to a given view.
 *
 * @param view The view to apply insets to.
 * @param mode Whether to apply the insets as MARGIN or PADDING.
 * @param applyTop Whether to apply the top inset.
 * @param applyBottom Whether to apply the bottom inset.
 */
fun applySystemBarInsets(
    view: View,
    mode: InsetMode = InsetMode.MARGIN,
    applyTop: Boolean = false,
    applyBottom: Boolean = true
) {
    // This is optional, as the listener will overwrite the values anyway.
    // It can be useful to see an immediate change before the first inset pass.
    resetInsets(view, mode, applyTop, applyBottom)

    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

        when (mode) {
            InsetMode.MARGIN -> {
                val params = v.layoutParams as MarginLayoutParams
                if (applyTop) {
                    params.topMargin = systemBars.top
                }
                if (applyBottom) {
                    params.bottomMargin = systemBars.bottom
                }
                v.layoutParams = params
            }
            InsetMode.PADDING -> {
                // Important: Preserve existing horizontal padding.
                v.setPadding(
                    v.paddingLeft,
                    if (applyTop) systemBars.top else v.paddingTop,
                    v.paddingRight,
                    if (applyBottom) systemBars.bottom else v.paddingBottom
                )
            }
        }

        // Return the insets so other views can also use them
        insets
    }

    // Request the insets to be applied
    view.requestApplyInsets()
}

/**
 * Resets the top/bottom margin or padding of a view to 0.
 */
private fun resetInsets(
    view: View,
    mode: InsetMode,
    applyTop: Boolean,
    applyBottom: Boolean
) {
    when (mode) {
        InsetMode.MARGIN -> {
            val params = view.layoutParams as MarginLayoutParams
            if (applyTop) params.topMargin = 0
            if (applyBottom) params.bottomMargin = 0
            view.layoutParams = params
        }
        InsetMode.PADDING -> {
            view.setPadding(
                view.paddingLeft,
                if (applyTop) 0 else view.paddingTop,
                view.paddingRight,
                if (applyBottom) 0 else view.paddingBottom
            )
        }
    }
}