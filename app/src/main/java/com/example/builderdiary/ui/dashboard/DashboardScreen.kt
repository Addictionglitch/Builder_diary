package com.example.builderdiary.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind // <--- FIXED: Added missing import
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.builderdiary.AccentYellow
import com.example.builderdiary.DarkBackground
import com.example.builderdiary.TextGrey
import com.example.builderdiary.TextWhite
import com.example.builderdiary.data.local.entity.ProjectEntity

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onProjectClick: (Long) -> Unit,
    onAddProjectClick: () -> Unit,
    onBack: () -> Unit // Ensure this parameter exists
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // GESTURE CONFIGURATION
    val density = LocalDensity.current
    val minSwipeDistance = with(density) { 100.dp.toPx() }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground) // Ensure background is set
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        // SWIPE DOWN: Trigger if dragged down (positive value) significantly
                        if (dragOffset > minSwipeDistance) {
                            onBack()
                        }
                        dragOffset = 0f
                    },
                    onDragCancel = { dragOffset = 0f }
                ) { change, dragAmount ->
                    dragOffset += dragAmount
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.DarkGray)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "TOTAL FOCUS TIME",
                color = AccentYellow,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )

            Text(
                text = uiState.totalFocusHours,
                color = TextWhite,
                fontSize = 48.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Add New Project Button
                item {
                    NewProjectCard(onClick = onAddProjectClick)
                }

                // Project Items
                items(uiState.projects) { project ->
                    ProjectCard(project = project, onClick = { onProjectClick(project.id) })
                }
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(
                color = AccentYellow,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun ProjectCard(project: ProjectEntity, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Square cards
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Archetype Icon/Label
                Text(
                    text = project.archetype.name.take(3),
                    color = TextGrey,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                // Level Badge
                Box(
                    modifier = Modifier
                        .background(AccentYellow, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "LVL ${project.currentLevel}",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = project.name.uppercase(),
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun NewProjectCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            // Dashed Border manually drawn
            .drawBehind {
                drawRoundRect(
                    color = Color.DarkGray,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = TextGrey,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "NEW PROJECT",
                color = TextGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}