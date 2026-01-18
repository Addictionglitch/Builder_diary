package com.example.builderdiary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.builderdiary.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    fun getProjectById(id: Long): Flow<ProjectEntity>

    @Insert
    suspend fun insertProject(project: ProjectEntity): Long

    @Query("UPDATE projects SET totalFocusSeconds = totalFocusSeconds + :addedTime WHERE id = :id")
    suspend fun updateProjectStats(id: Long, addedTime: Long)
}