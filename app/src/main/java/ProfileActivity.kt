package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fitnessapp.com.example.fitnessapp.FirebaseAuthService
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var authService: FirebaseAuthService
    private lateinit var firestoreService: FirestoreService

    private lateinit var backButton: ImageView
    private lateinit var profileImage: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var emailText: TextView
    private lateinit var ageEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var fitnessLevelSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var logoutButton: Button
    private lateinit var progressBar: ProgressBar

    private var userProfile: UserProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase services
        authService = FirebaseAuthService()
        firestoreService = FirestoreService()

        initializeViews()
        setupSpinner()
        setupClickListeners()
        loadUserProfile()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.back_button)
        profileImage = findViewById(R.id.profile_image)
        nameEditText = findViewById(R.id.name_edit_text)
        emailText = findViewById(R.id.email_text)
        ageEditText = findViewById(R.id.age_edit_text)
        heightEditText = findViewById(R.id.height_edit_text)
        weightEditText = findViewById(R.id.weight_edit_text)
        fitnessLevelSpinner = findViewById(R.id.fitness_level_spinner)
        saveButton = findViewById(R.id.save_button)
        logoutButton = findViewById(R.id.logout_button)
        progressBar = findViewById(R.id.progress_bar)
    }

    private fun setupSpinner() {
        val fitnessLevels = arrayOf("Beginner", "Intermediate", "Advanced")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fitnessLevels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fitnessLevelSpinner.adapter = adapter
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener { finish() }
        saveButton.setOnClickListener { saveProfile() }
        logoutButton.setOnClickListener { showLogoutDialog() }
        profileImage.setOnClickListener { changeProfileImage() }
    }

    private fun loadUserProfile() {
        val userId = authService.getCurrentUser()?.uid ?: run {
            // User not authenticated, redirect to login
            redirectToLogin()
            return
        }

        progressBar.visibility = android.view.View.VISIBLE

        lifecycleScope.launch {
            try {
                val result = firestoreService.getUserProfile(userId)
                result.onSuccess { profile ->
                    userProfile = profile
                    populateUserData(profile)
                    progressBar.visibility = android.view.View.GONE
                }.onFailure { exception ->
                    progressBar.visibility = android.view.View.GONE
                    // If profile doesn't exist, create a basic one
                    createBasicProfile(userId)
                }
            } catch (e: Exception) {
                progressBar.visibility = android.view.View.GONE
                showError("Error loading profile: ${e.message}")
            }
        }
    }

    private fun createBasicProfile(userId: String) {
        val user = authService.getCurrentUser()
        if (user != null) {
            val basicProfile = UserProfile(
                uid = userId,
                displayName = user.displayName ?: "",
                email = user.email ?: "",
                createdAt = System.currentTimeMillis()
            )
            userProfile = basicProfile
            populateUserData(basicProfile)
        }
    }

    private fun populateUserData(profile: UserProfile) {
        nameEditText.setText(profile.displayName)
        emailText.text = profile.email
        ageEditText.setText(if (profile.age > 0) profile.age.toString() else "")
        heightEditText.setText(if (profile.height > 0) profile.height.toString() else "")
        weightEditText.setText(if (profile.weight > 0) profile.weight.toString() else "")

        // Set fitness level spinner
        val fitnessLevels = arrayOf("Beginner", "Intermediate", "Advanced")
        val position = fitnessLevels.indexOf(profile.fitnessLevel)
        if (position >= 0) {
            fitnessLevelSpinner.setSelection(position)
        }
    }

    private fun saveProfile() {
        val userId = authService.getCurrentUser()?.uid ?: return

        val name = nameEditText.text.toString().trim()
        val age = ageEditText.text.toString().trim()
        val height = heightEditText.text.toString().trim()
        val weight = weightEditText.text.toString().trim()
        val fitnessLevel = fitnessLevelSpinner.selectedItem.toString()

        if (!validateInput(name, age, height, weight)) return

        progressBar.visibility = android.view.View.VISIBLE

        val updates = mapOf(
            "displayName" to name,
            "age" to (age.toIntOrNull() ?: 0),
            "height" to (height.toDoubleOrNull() ?: 0.0),
            "weight" to (weight.toDoubleOrNull() ?: 0.0),
            "fitnessLevel" to fitnessLevel
        )

        lifecycleScope.launch {
            try {
                val result = if (userProfile != null) {
                    // Update existing profile
                    firestoreService.updateUserProfile(userId, updates)
                } else {
                    // Create new profile
                    val user = authService.getCurrentUser()
                    val newProfile = UserProfile(
                        uid = userId,
                        displayName = name,
                        email = user?.email ?: "",
                        age = age.toIntOrNull() ?: 0,
                        height = height.toDoubleOrNull() ?: 0.0,
                        weight = weight.toDoubleOrNull() ?: 0.0,
                        fitnessLevel = fitnessLevel,
                        createdAt = System.currentTimeMillis()
                    )
                    firestoreService.createUserProfile(newProfile)
                }

                result.onSuccess {
                    progressBar.visibility = android.view.View.GONE
                    Toast.makeText(this@ProfileActivity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }.onFailure { exception ->
                    progressBar.visibility = android.view.View.GONE
                    showError("Failed to update profile: ${exception.message}")
                }
            } catch (e: Exception) {
                progressBar.visibility = android.view.View.GONE
                showError("Error updating profile: ${e.message}")
            }
        }
    }

    private fun validateInput(name: String, age: String, height: String, weight: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            nameEditText.error = "Name is required"
            isValid = false
        }

        if (age.isNotEmpty()) {
            val ageValue = age.toIntOrNull()
            if (ageValue == null || ageValue < 10 || ageValue > 120) {
                ageEditText.error = "Enter a valid age (10-120)"
                isValid = false
            }
        }

        if (height.isNotEmpty()) {
            val heightValue = height.toDoubleOrNull()
            if (heightValue == null || heightValue < 50 || heightValue > 300) {
                heightEditText.error = "Enter a valid height in cm (50-300)"
                isValid = false
            }
        }

        if (weight.isNotEmpty()) {
            val weightValue = weight.toDoubleOrNull()
            if (weightValue == null || weightValue < 20 || weightValue > 500) {
                weightEditText.error = "Enter a valid weight in kg (20-500)"
                isValid = false
            }
        }

        return isValid
    }

    private fun changeProfileImage() {
        Toast.makeText(this, "Profile image feature coming soon!", Toast.LENGTH_SHORT).show()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                authService.signOut()
                redirectToLogin()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun redirectToLogin() {
        try {
            val intent = Intent(this, login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            // If LoginActivity doesn't exist, just finish this activity
            finish()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}