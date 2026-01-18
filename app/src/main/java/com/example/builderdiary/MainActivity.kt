package com.example.builderdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.builderdiary.ui.navigation.FocusAppNavHost

// --- Colors ---
val DarkBackground = Color(0xFF000000)
val AccentYellow = Color(0xFFFFD000)
val TextWhite = Color(0xFFFFFFFF)
val TextGrey = Color(0xFF8E8E93)

// --- UPDATED Glassmorphism Brushes ---

// 1. Weaker Edge Light: Less contrast between top and bottom.
val GlassEdgeBrush = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.2f), // Reduced from 0.15f -> More subtle highlight
        Color.White.copy(alpha = 0.06f)  // Increased slightly from 0.02f -> Smoother fade
    )
)

// 2. Unified Surface: "Dark Grey" to "Slightly Darker Grey" (Instead of Grey -> Black)
val GlassBackgroundBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF222224).copy(alpha = 0.95f), // Very dark grey, almost opaque
        Color(0xFF161618).copy(alpha = 0.95f)  // Only slightly darker at bottom
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(background = DarkBackground)
            ) {
                FocusAppNavHost()
            }
        }
    }
}