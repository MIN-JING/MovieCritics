package com.jim.moviecritics.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.jim.moviecritics.R

/**
 * Compose counterpart of the XML theme `Theme.Material3.MovieCritics`.
 *
 * Colours are read from res/values/colors.xml, so the XML and Compose screens share one
 * source. Roles the XML theme does not set keep the Material 3 baseline values.
 */
@Composable
fun MovieCriticsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) nightColorScheme() else dayColorScheme(),
        content = content
    )
}

/** Mirrors values/themes.xml. */
@Composable
private fun dayColorScheme(): ColorScheme = lightColorScheme(
    primary = colorResource(R.color.primary),
    onPrimary = colorResource(R.color.onPrimary),
    primaryContainer = colorResource(R.color.primaryContainer),
    onPrimaryContainer = colorResource(R.color.onPrimaryContainer),
    secondary = colorResource(R.color.secondary),
    onSecondary = colorResource(R.color.onSecondary),
    secondaryContainer = colorResource(R.color.secondaryContainer),
    onSecondaryContainer = colorResource(R.color.onSecondaryContainer)
)

/**
 * Mirrors values-night/themes.xml. The night theme only darkens the brand colours and keeps a
 * light background and surface, so it is built on the light baseline: dark baseline values for
 * the unset roles (surface containers, onSurfaceVariant, ...) would be unreadable on a light
 * surface.
 */
@Composable
private fun nightColorScheme(): ColorScheme = lightColorScheme(
    primary = colorResource(R.color.primary_night),
    onPrimary = colorResource(R.color.onPrimary_night),
    primaryContainer = colorResource(R.color.primaryContainer_night),
    onPrimaryContainer = colorResource(R.color.onPrimaryContainer_night),
    secondary = colorResource(R.color.secondary_night),
    onSecondary = colorResource(R.color.onSecondary_night),
    secondaryContainer = colorResource(R.color.secondaryContainer_night),
    onSecondaryContainer = colorResource(R.color.onSecondaryContainer_night),
    background = colorResource(R.color.background),
    onBackground = colorResource(R.color.colorOnBackground),
    surface = colorResource(R.color.surface),
    onSurface = colorResource(R.color.onSurface)
)
