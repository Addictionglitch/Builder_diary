package com.example.builderdiary.ui.receipt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.builderdiary.data.local.entity.SessionEntity
import com.example.builderdiary.data.repository.FocusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionReceiptViewModel @Inject constructor(
    private val repository: FocusRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Retrieve arguments
    val xpEarned: Int = savedStateHandle.get<Int>("xpEarned") ?: 0
    val durationSeconds: Long = savedStateHandle.get<Long>("duration") ?: 0L
    // FIXED: Retrieve projectId
    val projectId: Long = savedStateHandle.get<Long>("projectId") ?: 1L
    
    var notes by mutableStateOf("")
    var isSaving by mutableStateOf(false)

    fun commitSession(onComplete: () -> Unit) {
        if (isSaving) return
        isSaving = true

        viewModelScope.launch {
            repository.insertSession(
                SessionEntity(
                    projectId = projectId,
                    startTime = System.currentTimeMillis() - (durationSeconds * 1000),
                    durationSeconds = durationSeconds,
                    xpEarned = xpEarned,
                    notes = notes
                )
            )
            onComplete()
        }
    }
}
