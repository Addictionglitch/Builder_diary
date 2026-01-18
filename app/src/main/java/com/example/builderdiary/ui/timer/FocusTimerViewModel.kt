package com.example.builderdiary.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.builderdiary.data.local.entity.ProjectEntity
import com.example.builderdiary.data.repository.FocusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FocusTimerViewModel @Inject constructor(
    private val repository: FocusRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    // One-shot event for navigation
    private val _navigationEvent = Channel<TimerNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private var timerJob: Job? = null
    // Changed default to 10 seconds for testing purposes, change back to 25*60 later
    private val defaultSessionTime = 25 * 60L

    init {
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            repository.createInitialProjectIfNoneExists()
            val projects = repository.getAllProjects().first()
            _uiState.value = _uiState.value.copy(
                projectList = projects,
                selectedProject = projects.firstOrNull()
            )
        }
    }

    fun toggleTimer() {
        if (_uiState.value.isTimerRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isTimerRunning = true)

        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeft > 0) {
                delay(1000L)
                val newTime = _uiState.value.timeLeft - 1
                // Prevent division by zero
                val progress = if (defaultSessionTime > 0) newTime.toFloat() / defaultSessionTime.toFloat() else 0f

                _uiState.value = _uiState.value.copy(
                    timeLeft = newTime,
                    progress = progress
                )
            }
            finishSession()
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isTimerRunning = false)
    }

    private fun finishSession() {
        pauseTimer()
        _uiState.value = _uiState.value.copy(timeLeft = defaultSessionTime, progress = 1f)

        viewModelScope.launch {
            _navigationEvent.send(
                TimerNavigationEvent.SessionComplete(
                    xp = 150, // Example calculation
                    duration = defaultSessionTime,
                    projectId = _uiState.value.selectedProject?.id ?: 0L
                )
            )
        }
    }

    fun selectProject(project: ProjectEntity) {
        _uiState.value = _uiState.value.copy(selectedProject = project)
    }
}

sealed class TimerNavigationEvent {
    data class SessionComplete(val xp: Int, val duration: Long, val projectId: Long) : TimerNavigationEvent()
}

data class TimerUiState(
    val timeLeft: Long = 25 * 60L,
    val isTimerRunning: Boolean = false,
    val progress: Float = 1.0f,
    val selectedProject: ProjectEntity? = null,
    val projectList: List<ProjectEntity> = emptyList()
)