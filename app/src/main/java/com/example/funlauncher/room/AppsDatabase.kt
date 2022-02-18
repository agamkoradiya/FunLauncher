package com.example.funlauncher.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.funlauncher.room.model.AppDbModel

@Database(
    entities = [AppDbModel::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppsDatabase : RoomDatabase() {

    abstract fun getAppsDao(): AppsDao

}