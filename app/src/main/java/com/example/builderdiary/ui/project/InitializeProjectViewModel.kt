package com.example.builderdiary.ui.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.builderdiary.data.local.entity.Archetype
import com.example.builderdiary.data.repository.FocusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InitializeProjectViewModel @Inject constructor(
    private val repository: FocusRepository
) : ViewModel() {

    var name by mutableStateOf("")
    // FIXED: Use the Entity enum 'Archetype' directly
    var selectedArchetype by mutableStateOf(Archetype.DEV)
    var selectedColor by mutableStateOf(0xFFFFC107) // Default to a yellow/amber color

    fun createProject(onProjectCreated: () -> Unit) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                repository.insertProject(
                    name = name,
                    archetype = selectedArchetype,
                    color = selectedColor
                )
                onProjectCreated()
            }
        }
    }
}