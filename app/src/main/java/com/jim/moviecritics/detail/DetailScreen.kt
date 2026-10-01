package com.jim.moviecritics.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun DetailScreen(
    itemId: Int,
    viewModel: DetailViewModel?,
    onBackPressed: () -> Unit
) {
    // Fetch item details using the viewModel and itemId
//    val itemDetails by viewModel.itemDetails.collectAsStateWithLifecycle()

    // UI for detail screen
    Column(modifier = Modifier.fillMaxSize()) {
        // Top app bar with back button
        TopAppBar(
            title = { Text(text = "ID: $itemId") },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        // Detail content
        // ...
    }
}

@Preview(showBackground = true, name = "Detail Screen Preview")
@Composable
fun DetailScreenPreview() {
    DetailScreen(
        itemId = 1,
        viewModel = null,
        onBackPressed = {}
    )
}