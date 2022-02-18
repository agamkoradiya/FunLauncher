package com.example.funlauncher.room.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appDbModel")
data class AppDbModel(
    @PrimaryKey(autoGenerate = false)
    val appPackageName: String,
//    val originalAppName: String,
    val customAppName: String?,
    val customAppIcon: Bitmap?
)