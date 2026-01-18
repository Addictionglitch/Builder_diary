package com.example.builderdiary.ui.theme

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Shared Modifier for the Neon Glow effect
fun Modifier.neonGlow(
    color: Color,
    blurRadius: Dp = 10.dp
) = this.drawBehind {
    drawIntoCanvas {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = color.toArgb()
        frameworkPaint.maskFilter = BlurMaskFilter(
            blurRadius.toPx(),
            BlurMaskFilter.Blur.NORMAL
        )
        it.drawRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            paint = paint
        )
    }
}