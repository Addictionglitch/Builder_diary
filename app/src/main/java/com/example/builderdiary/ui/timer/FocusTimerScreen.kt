package com.example.builderdiary.ui.timer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
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
import com.example.builderdiary.ui.dashboard.DashboardScreen
import com.example.builderdiary.ui.project.InitializeProjectScreen
import com.example.builderdiary.ui.project.ProjectDetailScreen
import com.example.builderdiary.ui.receipt.SessionReceiptScreen
import com.example.builderdiary.ui.settings.SettingsScreen
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class SessionReceiptState(
    val xp: Int,
    val duration: Long,
    val projectId: Long
)

@Composable
fun FocusTimerScreen(
    viewModel: FocusTimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    // --- SCREEN DIMENSIONS ---
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightPx = with(LocalDensity.current) { screenHeight.toPx() }
    val screenWidthPx = with(LocalDensity.current) { screenWidth.toPx() }

    // --- OVERLAY STATES ---
    val dashboardOffset = remember { Animatable(screenHeightPx) }
    val settingsOffset = remember { Animatable(screenWidthPx) }
    var sessionReceiptState by remember { mutableStateOf<SessionReceiptState?>(null) }

    // --- NAVIGATION EVENT LISTENER ---
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is TimerNavigationEvent.SessionComplete -> {
                    sessionReceiptState = SessionReceiptState(event.xp, event.duration, event.projectId)
                }
            }
        }
    }

    // --- BACK HANDLING ---
    BackHandler(
        enabled = uiState.isCreatingProject || uiState.activeProjectId != null || sessionReceiptState != null || settingsOffset.value < screenWidthPx || dashboardOffset.value < screenHeightPx
    ) {
        scope.launch {
            when {
                uiState.isCreatingProject -> viewModel.closeProjectCreation()
                uiState.activeProjectId != null -> viewModel.closeProjectDetail()
                sessionReceiptState != null -> sessionReceiptState = null
                settingsOffset.value < screenWidthPx -> settingsOffset.animateTo(
                    screenWidthPx,
                    spring(stiffness = Spring.StiffnessMedium)
                )
                dashboardOffset.value < screenHeightPx -> dashboardOffset.animateTo(
                    screenHeightPx,
                    spring(stiffness = Spring.StiffnessLow)
                )
            }
        }
    }

    // --- UI ROOT ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // --- LAYER 1: TIMER (BASE) ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                val target = if (dashboardOffset.value < screenHeightPx * 0.9f) 0f else screenHeightPx
                                dashboardOffset.animateTo(target, spring(stiffness = Spring.StiffnessLow))
                            }
                        }
                    ) { _, dragAmount ->
                        if (dragAmount < 0 || dashboardOffset.value < screenHeightPx) {
                            scope.launch {
                                val newOffset = (dashboardOffset.value + dragAmount).coerceIn(0f, screenHeightPx)
                                dashboardOffset.snapTo(newOffset)
                            }
                        }
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopHeaderBar(
                    selectedProject = uiState.selectedProject,
                    projectList = uiState.projectList,
                    onProjectSelected = viewModel::selectProject,
                    onSettingsClicked = {
                        scope.launch { settingsOffset.animateTo(0f, spring(stiffness = Spring.StiffnessMedium)) }
                    }
                )
                Spacer(Modifier.weight(1f))
                TimerContent(
                    progress = uiState.progress,
                    timeLeft = uiState.timeLeft
                )
                Spacer(Modifier.weight(1f))
                SwipeableControlBar(
                    isRunning = uiState.isTimerRunning,
                    onStart = viewModel::toggleTimer,
                    onStop = {
                        viewModel.stopTimer()
                    }
                )
            }
        }

        // --- LAYER 2: DASHBOARD (BOTTOM SHEET) ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, dashboardOffset.value.roundToInt()) }
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                val target = if (dashboardOffset.value > screenHeightPx * 0.15f) screenHeightPx else 0f
                                dashboardOffset.animateTo(target, spring(stiffness = Spring.StiffnessLow))
                            }
                        }
                    ) { _, dragAmount ->
                        scope.launch {
                            val newOffset = (dashboardOffset.value + dragAmount).coerceIn(0f, screenHeightPx)
                            dashboardOffset.snapTo(newOffset)
                        }
                    }
                }
        ) {
            DashboardScreen(
                onProjectClick = { viewModel.openProjectDetail(it) },
                onAddProjectClick = { viewModel.openProjectCreation() },
                onBack = {
                    scope.launch { dashboardOffset.animateTo(screenHeightPx, spring(stiffness = Spring.StiffnessLow)) }
                }
            )
        }

        // --- LAYER 3: SETTINGS (SIDE SHEET) ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(settingsOffset.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                val target = if (settingsOffset.value > screenWidthPx * 0.85f) screenWidthPx else 0f
                                settingsOffset.animateTo(target, spring(stiffness = Spring.StiffnessMedium))
                            }
                        }
                    ) { _, dragAmount ->
                        scope.launch {
                            val newOffset = (settingsOffset.value + dragAmount).coerceIn(0f, screenWidthPx)
                            settingsOffset.snapTo(newOffset)
                        }
                    }
                }
        ) {
            SettingsScreen(
                onBack = {
                    scope.launch { settingsOffset.animateTo(screenWidthPx, spring(stiffness = Spring.StiffnessMedium)) }
                }
            )
        }
        
        // --- LAYER 4: PROJECT DETAIL ---
        AnimatedVisibility(
            visible = uiState.activeProjectId != null,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            uiState.activeProjectId?.let { projectId ->
                ProjectDetailScreen(
                    projectId = projectId,
                    onBack = { viewModel.closeProjectDetail() },
                    onStartSession = {
                        viewModel.selectProject(uiState.projectList.first { p -> p.id == it })
                        viewModel.closeProjectDetail()
                    }
                )
            }
        }

        // --- LAYER 5: INITIALIZE PROJECT ---
        AnimatedVisibility(
            visible = uiState.isCreatingProject,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            InitializeProjectScreen(
                onProjectCreated = { viewModel.closeProjectCreation() },
                onBack = { viewModel.closeProjectCreation() }
            )
        }

        // --- LAYER 6: SESSION RECEIPT (MODAL) ---
        AnimatedVisibility(
            visible = sessionReceiptState != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            SessionReceiptScreen(
                onComplete = { sessionReceiptState = null }
            )
        }
    }
}

@Composable
fun SwipeableControlBar(
    isRunning: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    
    // 0f = Left, 1f = Right
    val swipeProgress = remember { Animatable(0f) }
    
    // Dimensions
    val barHeight = 76.dp
    val thumbSize = 68.dp
    val padding = 4.dp
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(barHeight)
            .border(1.dp, GlassEdgeBrush, RoundedCornerShape(50))
            .clip(RoundedCornerShape(50))
            .background(GlassBackgroundBrush)
    ) {
        val maxWidthPx = with(LocalDensity.current) { (LocalConfiguration.current.screenWidthDp.dp - (padding*2)).toPx() }
        val thumbSizePx = with(density) { thumbSize.toPx() }
        val paddingPx = with(density) { padding.toPx() }
        val dragRange = maxWidthPx - thumbSizePx - (paddingPx * 2)

        // 1. Progress Fill
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(padding)
                .height(thumbSize) // Match thumb height
                .width(with(density) { (thumbSize.toPx() + (swipeProgress.value * dragRange)).toDp() })
                .clip(RoundedCornerShape(50))
                .background(AccentYellow.copy(alpha = 0.2f * swipeProgress.value))
        )

        // 2. Label
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 1f - swipeProgress.value },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isRunning) "SLIDE TO FINISH" else "SLIDE TO FOCUS",
                color = TextGrey.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
            Icon(
                imageVector = Icons.Default.KeyboardDoubleArrowRight,
                contentDescription = null,
                tint = AccentYellow.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 24.dp)
            )
        }

        // 3. Thumb
        Box(
            modifier = Modifier
                .offset { IntOffset(x = (swipeProgress.value * dragRange + paddingPx).roundToInt(), y = 0) }
                .align(Alignment.CenterStart)
                .size(thumbSize)
                .pointerInput(isRunning) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (swipeProgress.value >= 0.85f) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (isRunning) onStop() else onStart()
                            }
                            coroutineScope.launch { 
                                swipeProgress.animateTo(0f, spring(stiffness = Spring.StiffnessMedium)) 
                            }
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        val newProgress = (swipeProgress.value + dragAmount / dragRange).coerceIn(0f, 1f)
                        coroutineScope.launch { swipeProgress.snapTo(newProgress) }
                    }
                }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                    .clip(CircleShape)
                    .background(if (swipeProgress.value > 0.8f) AccentYellow else Color(0xFF28282A))
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = if (swipeProgress.value > 0.8f) Color.Black else TextWhite
                )
            }
        }
    }
}

// All helper composables remain the same...
@Composable
fun TopHeaderBar(
    selectedProject: ProjectEntity?,
    projectList: List<ProjectEntity>,
    onProjectSelected: (ProjectEntity) -> Unit,
    onSettingsClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassIconButton(icon = Icons.Outlined.Info)

        ProjectSelector(
            selectedProject = selectedProject,
            projectList = projectList,
            onProjectSelected = onProjectSelected
        )

        Box(modifier = Modifier.clickable { onSettingsClicked() }) {
            GlassIconButton(icon = Icons.Outlined.Settings)
        }
    }
}

@Composable
fun ProjectSelector(
    selectedProject: ProjectEntity?,
    projectList: List<ProjectEntity>,
    onProjectSelected: (ProjectEntity) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

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
}

@Composable
fun TimerContent(progress: Float, timeLeft: Long) {
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeFormatted = java.util.Locale.getDefault().let {
        String.format(it, "%02d:%02d", minutes, seconds)
    }

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
            drawIntoCanvas { canvas ->
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
                canvas.drawArc(
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