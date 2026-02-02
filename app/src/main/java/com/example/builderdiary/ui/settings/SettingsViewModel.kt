package com.example.builderdiary.ui.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SettingsUiState(
    val focusDurationMinutes: Int = 25,
    val isSoundEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true,
    val version: String = "v1.0.0 (Beta)"
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    fun updateFocusDuration(minutes: Int) {
        _uiState.update { it.copy(focusDurationMinutes = minutes.coerceIn(5, 120)) }
    }

    fun toggleSound(enabled: Boolean) {
        _uiState.update { it.copy(isSoundEnabled = enabled) }
    }

    fun toggleVibration(enabled: Boolean) {
        _uiState.update { it.copy(isVibrationEnabled = enabled) }
    }
}