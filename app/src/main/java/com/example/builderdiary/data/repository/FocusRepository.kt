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

    // --- THIS WAS MISSING ---
    suspend fun insertProject(name: String, archetype: Archetype, color: Long) {
        val colorHex = String.format("#%06X", (0xFFFFFF and color.toInt()))
        projectDao.insertProject(
            ProjectEntity(
                name = name,
                archetype = archetype,
                colorHex = colorHex
            )
        )
    }

    // Insert a completed session
    suspend fun insertSession(session: SessionEntity) {
        sessionDao.insertSession(session)
    }

    fun getAllProjects(): Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    fun getProjectById(id: Long): Flow<ProjectEntity> = projectDao.getProjectById(id)

    fun getSessionsForProject(projectId: Long): Flow<List<SessionEntity>> =
        sessionDao.getSessionsForProject(projectId)
}