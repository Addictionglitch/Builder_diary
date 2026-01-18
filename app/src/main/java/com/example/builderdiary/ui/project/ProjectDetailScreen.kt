package com.example.builderdiary.ui.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.builderdiary.AccentYellow
import com.example.builderdiary.TextGrey
import com.example.builderdiary.TextWhite
import com.example.builderdiary.data.local.entity.SessionEntity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

@Composable
fun ProjectDetailScreen(
    onStartSession: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: ProjectDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // --- SWIPE DOWN LOGIC (Nested Scroll) ---
    val density = LocalDensity.current
    val minSwipeDistance = with(density) { 100.dp.toPx() }
    
    // We track total drag to detect a deliberate swipe down
    var dragOffset by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // If the user swipes down (positive Y) and the list is at the top (available > 0)
                if (source == NestedScrollSource.Drag && available.y > 0) {
                    dragOffset += available.y
                    return available // We consume it so the UI doesn't bounce weirdly
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (dragOffset > minSwipeDistance) {
                    onBack()
                }
                dragOffset = 0f // Reset after fling
                return super.onPostFling(consumed, available)
            }
        }
    }

    // --- UI ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000)) // Strict Black
            .nestedScroll(nestedScrollConnection) // Attach the gesture listener
    ) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentYellow)
            }
        } else {
            Scaffold(
                containerColor = Color.Transparent,
                floatingActionButton = {
                    uiState.project?.let { project ->
                        FloatingActionButton(
                            onClick = { onStartSession(project.id) },
                            containerColor = AccentYellow,
                            shape = CircleShape,
                            modifier = Modifier.size(72.dp) // Large FAB
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Start Session",
                                tint = Color.Black,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp)
                ) {
                    uiState.project?.let { project ->
                        // Visual Handle for Swipe Down hint
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 12.dp)
                                .width(40.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.DarkGray)
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Header(
                            projectName = project.name,
                            totalTime = uiState.totalTimeFormatted,
                            level = project.currentLevel
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Timeline(sessions = uiState.sessions)
                    }
                }
            }
        }
    }
}

@Composable
fun Header(projectName: String, totalTime: String, level: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "LVL $level",
            color = AccentYellow,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .background(AccentYellow.copy(alpha = 0.15f), RoundedCornerShape(50))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = projectName.uppercase(),
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = TextWhite,
            lineHeight = 36.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "TOTAL ACTIVE TIME: $totalTime",
            fontSize = 14.sp,
            color = AccentYellow,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun Timeline(sessions: List<SessionEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                // Align line with the dots (15.dp margin + 5.dp center of 10.dp dot)
                val x = 15.dp.toPx() + 5.dp.toPx() 
                drawLine(
                    color = AccentYellow.copy(alpha = 0.3f),
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = strokeWidth
                )
            },
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        items(sessions) { session ->
            TimelineItem(session = session)
        }
    }
}

@Composable
fun TimelineItem(session: SessionEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Timeline Dot Column
        Column(
            modifier = Modifier
                .width(40.dp) // Fixed width for alignment
                .padding(start = 15.dp), // Indent
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(24.dp)) // Push dot down to align with Card top
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(AccentYellow, CircleShape)
            )
        }

        // Card Content
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val formattedDate = SimpleDateFormat("MMM dd", Locale.getDefault())
                        .format(Date(session.startTime))
                    
                    Text(
                        text = formattedDate.uppercase(),
                        color = TextGrey,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "${session.durationSeconds / 60}M",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                
                if (!session.notes.isNullOrBlank()) {
                    Text(
                        text = session.notes,
                        color = TextWhite,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                } else {
                    Text(
                        text = "Session Logged",
                        color = TextGrey.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // XP Tag
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "+${session.xpEarned} XP",
                        color = AccentYellow,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
