package com.example.builderdiary.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.builderdiary.data.local.entity.ProjectEntity
import com.example.builderdiary.data.repository.FocusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class DashboardUiState(
    val totalFocusHours: String = "0 HRS",
    val projects: List<ProjectEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val focusRepository: FocusRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            focusRepository.createInitialProjectIfNoneExists() // Ensure default project exists
            focusRepository.getAllProjects().collect { projects ->
                val totalSeconds = projects.sumOf { it.totalFocusSeconds.toDouble() }.toLong()
                val hours = TimeUnit.SECONDS.toHours(totalSeconds)
                val formattedHours = String.format("%,d HRS", hours)

                _uiState.update { currentState ->
                    currentState.copy(
                        projects = projects,
                        totalFocusHours = formattedHours,
                        isLoading = false
                    )
                }
            }
        }
    }
}