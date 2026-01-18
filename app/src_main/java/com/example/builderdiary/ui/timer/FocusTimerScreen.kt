package com.example.builderdiary.ui.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.builderdiary.AccentYellow
import com.example.builderdiary.DarkBackground
import com.example.builderdiary.GlassBackgroundBrush
import com.example.builderdiary.GlassEdgeBrush
import com.example.builderdiary.TextGrey
import com.example.builderdiary.TextWhite
import com.example.builderdiary.data.local.entity.ProjectEntity
import com.example.builderdiary.ui.theme.neonGlow

@Composable
fun FocusTimerScreen(
    viewModel: FocusTimerViewModel = hiltViewModel(),
    onDashboardClicked: () -> Unit,
    // FIXED: Updated signature to accept projectId
    navigateToSessionReceipt: (Int, Long, Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle Navigation Events
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is TimerNavigationEvent.SessionComplete -> {
                    // FIXED: Passing event.projectId
                    navigateToSessionReceipt(event.xp, event.duration, event.projectId)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopHeaderBar(
            selectedProject = uiState.selectedProject,
            projectList = uiState.projectList,
            onProjectSelected = viewModel::selectProject
        )

        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TimerContent(
                progress = uiState.progress,
                timeLeft = uiState.timeLeft
            )
        }

        BottomSlideBar(
            isRunning = uiState.isTimerRunning,
            onToggleTimer = viewModel::toggleTimer
        )
    }
}

// ... The rest of the UI components (TopHeaderBar, TimerContent, etc) remain the same ...
// For brevity, I am not repeating the helper composables unless you need them. 
// Just ensure TopHeaderBar, TimerContent, BottomSlideBar, GlassIconButton, NeonProgressRing are present.

@Composable
fun TopHeaderBar(
    selectedProject: ProjectEntity?,
    projectList: List<ProjectEntity>,
    onProjectSelected: (ProjectEntity) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassIconButton(icon = Icons.Outlined.Info)

        Box {
            Box(
                modifier = Modifier
                    .height(44.dp)
                    .border(1.dp, GlassEdgeBrush, RoundedCornerShape(50))
                    .clip(RoundedCornerShape(50))
                    .background(GlassBackgroundBrush)
                    .clickable { expanded = true }
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
                        text = selectedProject?.name?.uppercase() ?: "SELECT PROJECT",
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

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color(0xFF1C1C1E))
            ) {
                projectList.forEach { project ->
                    DropdownMenuItem(
                        text = { Text(project.name, color = TextWhite) },
                        onClick = {
                            onProjectSelected(project)
                            expanded = false
                        }
                    )
                }
            }
        }

        GlassIconButton(icon = Icons.Outlined.Settings)
    }
}

@Composable
fun TimerContent(progress: Float, timeLeft: Long) {
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            NeonProgressRing(progress = progress, radius = 140.dp)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeFormatted,
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
    }
}

@Composable
fun BottomSlideBar(isRunning: Boolean, onToggleTimer: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .border(1.dp, GlassEdgeBrush, RoundedCornerShape(50))
            .clip(RoundedCornerShape(50))
            .background(GlassBackgroundBrush)
            .clickable { onToggleTimer() }
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(68.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFF28282A))
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Toggle Timer",
                    tint = TextWhite,
                    modifier = Modifier.size(26.dp)
                )
            }

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = if (isRunning) "TAP TO PAUSE" else "TAP TO START",
                    color = TextGrey.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardDoubleArrowRight,
                contentDescription = null,
                tint = AccentYellow.copy(alpha = 0.8f),
                modifier = Modifier.size(24.dp).padding(end = 20.dp).neonGlow(AccentYellow, 15.dp)
            )
        }
    }
}

@Composable
fun GlassIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(50.dp)
            .border(1.dp, GlassEdgeBrush, CircleShape)
            .clip(CircleShape)
            .background(GlassBackgroundBrush)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = TextGrey, modifier = Modifier.size(22.dp))
    }
}

@Composable
fun NeonProgressRing(progress: Float, radius: Dp) {
    val ringStrokeWidth = 10.dp
    val glowRadius = 35.dp

    Box(modifier = Modifier.size(radius * 2), contentAlignment = Alignment.Center) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val size = this.size
            val strokePx = ringStrokeWidth.toPx()
            drawArc(
                color = Color(0xFF151515),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                topLeft = Offset(strokePx / 2, strokePx / 2),
                size = Size(size.width - strokePx, size.height - strokePx)
            )
            drawIntoCanvas {
                val paint = androidx.compose.ui.graphics.Paint().apply {
                    color = AccentYellow
                    style = PaintingStyle.Stroke
                    strokeWidth = strokePx
                    strokeCap = StrokeCap.Round
                }
                val frameworkPaint = paint.asFrameworkPaint()
                frameworkPaint.maskFilter = android.graphics.BlurMaskFilter(
                    glowRadius.toPx(),
                    android.graphics.BlurMaskFilter.Blur.NORMAL
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
