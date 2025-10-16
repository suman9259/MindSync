package com.example.mindsync.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.mindsync.presentation.theme.PrimaryGradientEnd
import com.example.mindsync.presentation.theme.PrimaryGradientStart

/**
 * Gradient background for screens
 */
@Composable
fun GradientBackground(
    startColor: Color = PrimaryGradientStart,
    endColor: Color = PrimaryGradientEnd,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(startColor, endColor)
                )
            ),
        content = content
    )
}