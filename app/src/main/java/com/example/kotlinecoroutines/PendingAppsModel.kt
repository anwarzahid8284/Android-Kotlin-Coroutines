package com.example.kotlinecoroutines

import android.graphics.drawable.Drawable

data class PendingAppsModel(
    val appPackageName: String,
    val appName: String,
    val appIcon: Drawable,
    val appOldVersion: String,
    var appNewVersion: String?
)