package com.example.ktan.ui.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun BoardScreenPreview() {
    MaterialTheme {
        BoardScreen(onQuit = {})
    }
}