package com.example.builderdiary.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.builderdiary.AccentYellow
import com.example.builderdiary.GlassBackgroundBrush
import com.example.builderdiary.GlassEdgeBrush
import com.example.builderdiary.TextGrey
import com.example.builderdiary.TextWhite

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Swipe Down Logic
    val density = LocalDensity.current
    val minSwipeDistance = with(density) { 100.dp.toPx() }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = { dragOffset = 0f }
                ) { change, dragAmount ->
                    change.consume()
                    dragOffset += dragAmount
                    if (dragOffset > minSwipeDistance) {
                        onBack()
                        dragOffset = 0f
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, GlassEdgeBrush, CircleShape)
                        .clip(CircleShape)
                        .background(GlassBackgroundBrush)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextGrey
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "SYSTEM CONFIG",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Settings List
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                
                SectionHeader("TIMER CONFIGURATION")
                
                // Focus Duration Control
                SettingsCard {
                    Column {
                        Text(
                            text = "FOCUS DURATION",
                            color = TextGrey,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${uiState.focusDurationMinutes} MIN",
                                color = AccentYellow,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Light,
                                fontFamily = FontFamily.Monospace
                            )
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ControlButton(icon = Icons.Default.Remove) {
                                    viewModel.updateFocusDuration(uiState.focusDurationMinutes - 5)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                ControlButton(icon = Icons.Default.Add) {
                                    viewModel.updateFocusDuration(uiState.focusDurationMinutes + 5)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader("HAPTICS & AUDIO")

                ToggleSetting(
                    label = "SOUND EFFECTS",
                    isChecked = uiState.isSoundEnabled,
                    onToggle = viewModel::toggleSound
                )

                ToggleSetting(
                    label = "HAPTIC FEEDBACK",
                    isChecked = uiState.isVibrationEnabled,
                    onToggle = viewModel::toggleVibration
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader("SYSTEM")
                
                SettingsCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "VERSION",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "BUILDER DIARY ${uiState.version}",
                                color = TextGrey,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = "// $title",
        color = Color.DarkGray,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GlassEdgeBrush, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(GlassBackgroundBrush)
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun ToggleSetting(label: String, isChecked: Boolean, onToggle: (Boolean) -> Unit) {
    SettingsCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle(!isChecked) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace
            )
            
            // Custom Switch Design
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (isChecked) AccentYellow.copy(alpha = 0.2f) else Color.Black)
                    .border(1.dp, if (isChecked) AccentYellow else Color.DarkGray, RoundedCornerShape(50))
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .align(if (isChecked) Alignment.CenterEnd else Alignment.CenterStart)
                        .padding(horizontal = 4.dp)
                        .clip(CircleShape)
                        .background(if (isChecked) AccentYellow else Color.Gray)
                )
            }
        }
    }
}

@Composable
fun ControlButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.05f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
    }
}