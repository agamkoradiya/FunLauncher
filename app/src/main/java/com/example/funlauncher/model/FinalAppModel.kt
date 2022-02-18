package com.example.funlauncher.model

import android.graphics.Bitmap
import android.graphics.drawable.Drawable

data class FinalAppModel(
    val appName: String,
    val appIcon: Drawable,
    val appPackageName: String,
    val customAppName: String?,
    val customAppIcon: Bitmap?,
    val isModified:Boolean = false
)