package com.example.builderdiary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val archetype: Archetype,
    val colorHex: String,
    val totalFocusSeconds: Long = 0,
    val currentLevel: Int = 1
)

enum class Archetype {
    DEV,
    WRITE,
    STUDY,
    TRAIN,
    FITNESS
}