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
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    private val repository: FocusRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectIdString: String? = savedStateHandle["projectId"]
    // Handle case where projectId might be passed as "1" or just 1
    private val projectId: Long = projectIdString?.toLongOrNull() ?: 1L

    val uiState: StateFlow<ProjectDetailUiState> = combine(
        repository.getProjectById(projectId),
        repository.getSessionsForProject(projectId)
    ) { project, sessions ->
        ProjectDetailUiState(
            project = project,
            // Sort by newest first
            sessions = sessions.sortedByDescending { it.startTime },
            totalTimeFormatted = formatSecondsToTimer(project?.totalFocusSeconds ?: 0)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProjectDetailUiState(isLoading = true)
    )

    private fun formatSecondsToTimer(totalSeconds: Long): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }
}

data class ProjectDetailUiState(
    val isLoading: Boolean = false,
    val project: ProjectEntity? = null,
    val sessions: List<SessionEntity> = emptyList(),
    val totalTimeFormatted: String = "00:00:00"
)
