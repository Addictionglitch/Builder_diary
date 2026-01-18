package com.example.builderdiary.ui.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.builderdiary.data.local.entity.Archetype

@Composable
fun InitializeProjectScreen(
    viewModel: InitializeProjectViewModel = hiltViewModel(),
    onProjectCreated: () -> Unit,
    onBack: () -> Unit
) {
    var totalDrag by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        // Reset after drag ends
                        totalDrag = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    totalDrag += dragAmount
                    
                    // Trigger exit if dragged down significantly (> 100 pixels)
                    if (totalDrag > 100f) {
                        onBack()
                        totalDrag = 0f // Prevent multiple calls
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Swipe Handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.DarkGray)
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "INITIALIZE PROJECT",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = { Text("// PROJECT DESIGNATION") },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFFFD000),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xFFFFD000),
                    unfocusedIndicatorColor = Color.Gray,
                    disabledIndicatorColor = Color.Gray,
                    focusedLabelColor = Color(0xFFFFD000),
                    unfocusedLabelColor = Color.Gray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            ArchetypeSelector(
                selectedArchetype = viewModel.selectedArchetype,
                onArchetypeSelected = { viewModel.selectedArchetype = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            ColorSelector(
                selectedColor = viewModel.selectedColor,
                onColorSelected = { viewModel.selectedColor = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.createProject(onProjectCreated) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD000))
            ) {
                Text(text = "CREATE PROJECT", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ArchetypeSelector(
    selectedArchetype: Archetype,
    onArchetypeSelected: (Archetype) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Archetype.values().forEach { archetype ->
            Chip(
                label = archetype.name,
                isSelected = selectedArchetype == archetype,
                onClick = { onArchetypeSelected(archetype) }
            )
        }
    }
}

@Composable
fun ColorSelector(
    selectedColor: Long,
    onColorSelected: (Long) -> Unit
) {
    val colors = listOf(0xFFF44336, 0xFFFF9800, 0xFF2196F3, 0xFF4CAF50, 0xFF9C27B0)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        colors.forEach { colorValue ->
            ColorCircle(
                color = Color(colorValue),
                isSelected = selectedColor == colorValue,
                onClick = { onColorSelected(colorValue) }
            )
        }
    }
}

@Composable
fun Chip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color(0xFFFFD000) else Color.DarkGray
    val contentColor = if (isSelected) Color.Black else Color.White

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = label, color = contentColor, fontSize = 12.sp)
    }
}

@Composable
fun ColorCircle(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, Color.White, CircleShape)
                } else {
                    Modifier
                }
            )
    )
}
