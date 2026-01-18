package com.example.builderdiary.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.builderdiary.data.local.dao.ProjectDao
import com.example.builderdiary.data.local.dao.SessionDao
import com.example.builderdiary.data.local.entity.ProjectEntity
import com.example.builderdiary.data.local.entity.SessionEntity

@Database(entities = [ProjectEntity::class, SessionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun sessionDao(): SessionDao
}