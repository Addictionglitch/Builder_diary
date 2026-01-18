package com.example.builderdiary.di

import android.content.Context
import androidx.room.Room
import com.example.builderdiary.data.local.AppDatabase
import com.example.builderdiary.data.local.dao.ProjectDao
import com.example.builderdiary.data.local.dao.SessionDao
import com.example.builderdiary.data.repository.FocusRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "focus_database"
        ).build()
    }

    @Provides
    fun provideProjectDao(appDatabase: AppDatabase): ProjectDao {
        return appDatabase.projectDao()
    }

    @Provides
    fun provideSessionDao(appDatabase: AppDatabase): SessionDao {
        return appDatabase.sessionDao()
    }

    @Provides
    @Singleton
    fun provideFocusRepository(projectDao: ProjectDao, sessionDao: SessionDao): FocusRepository {
        return FocusRepository(projectDao, sessionDao)
    }
}