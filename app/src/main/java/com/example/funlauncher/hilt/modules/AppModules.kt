package com.example.funlauncher.hilt.modules

import android.content.Context
import androidx.room.Room
import com.example.funlauncher.room.AppsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModules {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        AppsDatabase::class.java,
        "apps_database"
    ).build()

    @Singleton
    @Provides
    fun provideDao(database: AppsDatabase) = database.getAppsDao()
}