package com.example.builderdiary.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.builderdiary.AccentYellow
import com.example.builderdiary.GlassBackgroundBrush
import com.example.builderdiary.TextGrey
import com.example.builderdiary.TextWhite
import com.example.builderdiary.data.local.entity.ProjectEntity
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DashboardScreen(
    onProjectClick: (Long) -> Unit,
    onAddProjectClick: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AccentYellow)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Header(uiState.totalFocusHours)
            Spacer(modifier = Modifier.height(24.dp))
            ProjectGrid(
                projects = uiState.projects,
                onProjectClick = onProjectClick,
                onAddProjectClick = onAddProjectClick
            )
        }
    }
}

@Composable
fun Header(totalFocusHours: String) {
    Column {
        Text(
            text = "TOTAL FOCUS TIME",
            fontSize = 14.sp,
            color = TextGrey,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = totalFocusHours,
            fontSize = 48.sp,
            color = TextWhite,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "LEVEL 12 BUILDER",
            fontSize = 12.sp,
            color = AccentYellow,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProjectGrid(
    projects: List<ProjectEntity>,
    onProjectClick: (Long) -> Unit,
    onAddProjectClick: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AddNewProjectCard(onClick = onAddProjectClick)
        }
        items(projects) { project ->
            ProjectCard(project = project, onClick = { onProjectClick(project.id) })
        }
    }
}

@Composable
fun ProjectCard(project: ProjectEntity, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(GlassBackgroundBrush)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = project.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = project.archetype.name,
                    fontSize = 12.sp,
                    color = TextGrey
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(AccentYellow)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "LVL ${project.currentLevel}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun AddNewProjectCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.DarkGray.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "NEW PROJECT",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
    }
}