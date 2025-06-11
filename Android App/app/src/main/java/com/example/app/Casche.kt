package com.example.app

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.*

val colourButton = Color(0xFF2E8B57)
val colourBackground = Color(0xFFF3F1ED)
val colourSecondary = Color(0xFFD2B48C)
val colourSecondaryText = Color(0xFF5D5D5D)

@Composable
fun ItemUI(name: String, description: String) {
    Text(name)
}