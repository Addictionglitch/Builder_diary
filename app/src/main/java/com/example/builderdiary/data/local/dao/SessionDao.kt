package com.example.builderdiary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.builderdiary.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert
    suspend fun insertSession(session: SessionEntity)

    @Query("SELECT * FROM sessions WHERE projectId = :projectId")
    fun getSessionsForProject(projectId: Long): Flow<List<SessionEntity>>

    @Query("SELECT SUM(xpEarned) FROM sessions")
    fun getTotalXp(): Flow<Int>
}