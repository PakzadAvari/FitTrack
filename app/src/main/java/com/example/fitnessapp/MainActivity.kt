package com.example.fitnessapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        // Set your main activity layout here
        // setContentView(R.layout.activity_main)

        // For now, we'll just show a simple layout
        // You can replace this with your main app interface

        // Example: Set up your main app UI here
        setupMainInterface()
    }

    private fun setupMainInterface() {
        // Initialize your main app components here
        // Set up navigation, fragments, etc.

        // Example placeholder - replace with your actual implementation
        supportActionBar?.title = "Fit Track"
    }
}