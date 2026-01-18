package com.example.builderdiary.ui.project

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.builderdiary.data.local.entity.ProjectEntity
import com.example.builderdiary.data.local.entity.SessionEntity
import com.example.builderdiary.data.repository.FocusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    private val repository: FocusRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: Long = checkNotNull(savedStateHandle["projectId"])

    // Combine the project and sessions flows into one UI State
    val uiState: StateFlow<ProjectDetailUiState> = combine(
        repository.getProjectById(projectId),
        repository.getSessionsForProject(projectId)
    ) { project, sessions ->
        ProjectDetailUiState(
            project = project,
            sessions = sessions,
            // Simple helper to format total time (You can move this logic later)
            totalTimeFormatted = formatSecondsToHours(project?.totalFocusSeconds ?: 0)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProjectDetailUiState(isLoading = true)
    )

    private fun formatSecondsToHours(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        return "${hours}h ${minutes}m"
    }
}

// THE FIXED DATA CLASS
data class ProjectDetailUiState(
    val isLoading: Boolean = false,
    val project: ProjectEntity? = null,        // Now uses the real Entity
    val sessions: List<SessionEntity> = emptyList(), // Now uses the real Entity
    val totalTimeFormatted: String = "0h 0m"
)