package com.example.fitnessapp

import android.app.Application

class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Set default theme for testing
        setTheme(androidx.appcompat.R.style.Theme_AppCompat_Light)
    }
}