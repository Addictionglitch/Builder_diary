package com.example.builderdiary.ui.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.builderdiary.data.local.entity.ProjectEntity
import com.example.builderdiary.data.local.entity.SessionEntity
import com.example.builderdiary.data.repository.FocusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    private val repository: FocusRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectDetailUiState(isLoading = true))
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()

    fun loadProject(projectId: Long) {
        viewModelScope.launch {
            repository.getProjectById(projectId).collectLatest { project ->
                repository.getSessionsForProject(projectId).collectLatest { sessions ->
                    _uiState.value = ProjectDetailUiState(
                        project = project,
                        sessions = sessions.sortedByDescending { it.startTime },
                        totalTimeFormatted = formatSecondsToTimer(project?.totalFocusSeconds ?: 0)
                    )
                }
            }
        }
    }

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
