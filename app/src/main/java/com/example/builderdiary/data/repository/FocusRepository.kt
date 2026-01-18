package com.example.builderdiary.data.repository

import com.example.builderdiary.data.local.dao.ProjectDao
import com.example.builderdiary.data.local.dao.SessionDao
import com.example.builderdiary.data.local.entity.Archetype
import com.example.builderdiary.data.local.entity.ProjectEntity
import com.example.builderdiary.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FocusRepository @Inject constructor(
    private val projectDao: ProjectDao,
    private val sessionDao: SessionDao
) {
    suspend fun createInitialProjectIfNoneExists() {
        if (projectDao.getAllProjects().first().isEmpty()) {
            projectDao.insertProject(
                ProjectEntity(
                    name = "General",
                    archetype = Archetype.STUDY,
                    colorHex = "#FFFFFF"
                )
            )
        }
    }

    fun getAllProjects(): Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    fun getProjectById(id: Long): Flow<ProjectEntity> = projectDao.getProjectById(id)

    fun getSessionsForProject(projectId: Long): Flow<List<SessionEntity>> =
        sessionDao.getSessionsForProject(projectId)
}