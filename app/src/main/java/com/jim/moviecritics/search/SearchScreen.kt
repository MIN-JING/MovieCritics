package com.jim.moviecritics.search

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SearchScreen() {
    Surface {
        Text("Hello Compose")
    }
}

@Preview(showBackground = true, name = "Text preview")
@Composable
fun SearchScreenPreview() {
    MaterialTheme {
        SearchScreen()
    }
}