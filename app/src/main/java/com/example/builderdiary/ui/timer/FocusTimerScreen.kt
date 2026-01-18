package com.example.builderdiary.ui.timer

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.builderdiary.AccentYellow
import com.example.builderdiary.DarkBackground
import com.example.builderdiary.GlassBackgroundBrush
import com.example.builderdiary.GlassEdgeBrush
import com.example.builderdiary.TextGrey
import com.example.builderdiary.TextWhite


@Composable
fun FocusTimerScreen(
    onDashboardClicked: () -> Unit,
    navigateToProjectDetail: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopHeaderBar()

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TimerContent()
        }

        BottomSlideBar(onDashboardClicked)
    }
}

// --- Top Bar ---
@Composable
fun TopHeaderBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Button
        GlassIconButton(icon = Icons.Outlined.Info)

        // Center Pill
        Box(
            modifier = Modifier
                .height(44.dp)
                .border(1.dp, GlassEdgeBrush, RoundedCornerShape(50))
                .clip(RoundedCornerShape(50))
                .background(GlassBackgroundBrush)
                .padding(horizontal = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Icon(
                    imageVector = Icons.Default.BusinessCenter,
                    contentDescription = null,
                    tint = AccentYellow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "SAAS APP V1",
                    color = TextWhite.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = TextGrey,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Right Button
        GlassIconButton(icon = Icons.Outlined.Settings)
    }
}

// --- Timer Section ---
@Composable
fun TimerContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            NeonProgressRing(
                progress = 0.65f,
                radius = 140.dp
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "24:15",
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Light,
                    color = TextWhite,
                    letterSpacing = (-2).sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .neonGlow(AccentYellow, blurRadius = 8.dp)
                            .background(AccentYellow, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FOCUS MODE",
                        fontSize = 11.sp,
                        color = TextGrey,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(56.dp))

        SessionIndicators()
    }
}

@Composable
fun SessionIndicators() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Active
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(5.dp)
                    .neonGlow(AccentYellow, blurRadius = 12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(AccentYellow)
            )
            // Inactive
            repeat(3) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF1C1C1E))
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "SESSION 1/4",
            color = TextGrey.copy(alpha = 0.7f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.5.sp
        )
    }
}

// --- Bottom Slider ---
@Composable
fun BottomSlideBar(onDashboardClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .border(1.dp, GlassEdgeBrush, RoundedCornerShape(50))
            .clip(RoundedCornerShape(50))
            .background(GlassBackgroundBrush)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pause "Knob" - Slightly lighter for contrast
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(68.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFF28282A)) // Slightly lighter than background
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = TextWhite,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Text
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SLIDE TO FOCUS",
                    color = TextGrey.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    fontSize = 12.sp
                )
            }

            // Arrow
            Icon(
                imageVector = Icons.Default.KeyboardDoubleArrowRight,
                contentDescription = null,
                tint = AccentYellow.copy(alpha = 0.8f),
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 20.dp)
                    .neonGlow(AccentYellow, blurRadius = 15.dp)
            )
        }
    }
}

// --- Reusable Components ---

@Composable
fun GlassIconButton(icon: ImageVector) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(50.dp)
            .border(1.dp, GlassEdgeBrush, CircleShape)
            .clip(CircleShape)
            .background(GlassBackgroundBrush)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextGrey,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun NeonProgressRing(
    progress: Float,
    radius: Dp
) {
    val ringStrokeWidth = 10.dp
    val glowRadius = 35.dp

    Box(
        modifier = Modifier.size(radius * 2),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val size = this.size
            val strokePx = ringStrokeWidth.toPx()

            // 1. Background Track (Very Dark Grey)
            drawArc(
                color = Color(0xFF151515),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                topLeft = Offset(strokePx / 2, strokePx / 2),
                size = Size(size.width - strokePx, size.height - strokePx)
            )

            // 2. Glow Layer
            drawIntoCanvas {
                val paint = Paint().apply {
                    color = AccentYellow
                    style = PaintingStyle.Stroke
                    strokeWidth = strokePx
                    strokeCap = StrokeCap.Round
                }
                val frameworkPaint = paint.asFrameworkPaint()
                frameworkPaint.maskFilter = BlurMaskFilter(
                    glowRadius.toPx(),
                    BlurMaskFilter.Blur.NORMAL
                )
                it.drawArc(
                    left = strokePx / 2,
                    top = strokePx / 2,
                    right = size.width - strokePx / 2,
                    bottom = size.height - strokePx / 2,
                    startAngle = -90f,
                    sweepAngle = 360 * progress,
                    useCenter = false,
                    paint = paint
                )
            }

            // 3. Core Layer (The sharp yellow line)
            drawArc(
                color = AccentYellow,
                startAngle = -90f,
                sweepAngle = 360 * progress,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                topLeft = Offset(strokePx / 2, strokePx / 2),
                size = Size(size.width - strokePx, size.height - strokePx)
            )
        }
    }
}

fun Modifier.neonGlow(color: Color, blurRadius: Dp) = this.drawBehind {
    drawIntoCanvas {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = color.toArgb()
        frameworkPaint.maskFilter = BlurMaskFilter(
            blurRadius.toPx(),
            BlurMaskFilter.Blur.NORMAL
        )
        it.drawRect(
            left = 0f, top = 0f, right = size.width, bottom = size.height,
            paint = paint
        )
    }
}