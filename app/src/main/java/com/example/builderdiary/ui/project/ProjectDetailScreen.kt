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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.builderdiary.AccentYellow
import com.example.builderdiary.TextGrey
import com.example.builderdiary.TextWhite
import com.example.builderdiary.data.local.entity.SessionEntity
import java.text.SimpleDateFormat
import java.util.*
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProjectDetailScreen(
    onStartSession: (Long) -> Unit,
    viewModel: ProjectDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AccentYellow)
        }
    } else {
        Scaffold(
            floatingActionButton = {
                uiState.project?.let { project ->
                    FloatingActionButton(
                        onClick = { onStartSession(project.id) },
                        containerColor = AccentYellow,
                        shape = CircleShape,
                        modifier = Modifier.size(72.dp)
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
                    .padding(horizontal = 16.dp)
            ) {
                uiState.project?.let { project ->
                    Header(projectName = project.name, totalTime = uiState.totalTimeFormatted)
                    Spacer(modifier = Modifier.height(24.dp))
                    Timeline(sessions = uiState.sessions)
                }
            }
        }
    }
}

@Composable
fun Header(projectName: String, totalTime: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Text(
            text = projectName.uppercase(),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Text(
            text = "Total Active Time: $totalTime",
            fontSize = 14.sp,
            color = AccentYellow,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun Timeline(sessions: List<SessionEntity>) {
    LazyColumn(
        modifier = Modifier.drawBehind {
            val strokeWidth = 2.dp.toPx()
            val x = 16.dp.toPx()
            drawLine(
                color = TextGrey,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = strokeWidth
            )
        }
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
            .padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(AccentYellow, CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val formattedDate = SimpleDateFormat("MMM dd • mm'M'", Locale.getDefault())
                    .format(Date(session.startTime))
                Text(
                    text = formattedDate.uppercase(),
                    color = TextGrey,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                session.notes?.let { notes ->
                    Text(
                        text = notes,
                        color = TextWhite,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}