package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class signup : AppCompatActivity() {

    // Firebase Services
    private lateinit var authService: FirebaseAuthService
    private lateinit var firestoreService: FirestoreService

    // UI Components
    private lateinit var etName: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnSignUp: Button
    private lateinit var tvLogin: TextView
    private lateinit var progressIndicator: CircularProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase services
        initializeFirebase()

        // Check if user is already logged in
        if (authService.getCurrentUser() != null) {
            navigateToDashboard()
            return
        }

        initializeViews()
        setupClickListeners()
    }

    private fun initializeFirebase() {
        authService = FirebaseAuthService()
        firestoreService = FirestoreService()
    }

    private fun initializeViews() {
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvLogin = findViewById(R.id.tvLogin)

        // Add progress indicator if not in layout
        try {
            progressIndicator = findViewById(R.id.progressIndicator)
        } catch (e: Exception) {
            // Progress indicator not found in layout
        }
    }

    private fun setupClickListeners() {
        btnSignUp.setOnClickListener {
            if (validateInputs()) {
                performSignUp()
            }
        }

        tvLogin.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun validateInputs(): Boolean {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        // Validate name
        if (TextUtils.isEmpty(name)) {
            etName.error = "Please enter your name"
            etName.requestFocus()
            return false
        }

        if (name.length < 2) {
            etName.error = "Name must be at least 2 characters"
            etName.requestFocus()
            return false
        }

        // Validate phone
        if (TextUtils.isEmpty(phone)) {
            etPhone.error = "Please enter your phone number"
            etPhone.requestFocus()
            return false
        }

        if (phone.length < 10) {
            etPhone.error = "Please enter a valid phone number"
            etPhone.requestFocus()
            return false
        }

        // Validate email
        if (TextUtils.isEmpty(email)) {
            etEmail.error = "Please enter your email"
            etEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Please enter a valid email address"
            etEmail.requestFocus()
            return false
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            etPassword.error = "Please enter a password"
            etPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            etPassword.requestFocus()
            return false
        }

        // Check password strength
        if (!isPasswordStrong(password)) {
            etPassword.error = "Password must contain at least one number and one letter"
            etPassword.requestFocus()
            return false
        }

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.error = "Please confirm your password"
            etConfirmPassword.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            etConfirmPassword.requestFocus()
            return false
        }

        return true
    }

    private fun isPasswordStrong(password: String): Boolean {
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        return hasLetter && hasDigit
    }

    private fun performSignUp() {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        // Show loading state
        showLoading(true)

        // Perform Firebase authentication
        lifecycleScope.launch {
            try {
                // Create Firebase user account
                val result = authService.createUserWithEmailAndPassword(email, password)

                result.onSuccess { user ->
                    // Account created successfully, now create user profile
                    createUserProfile(user.uid, name, phone, email)
                }.onFailure { exception ->
                    // Account creation failed
                    onSignUpFailure(exception.message ?: "Account creation failed")
                }
            } catch (e: Exception) {
                onSignUpFailure(e.message ?: "An unexpected error occurred")
            }
        }
    }

    private suspend fun createUserProfile(userId: String, name: String, phone: String, email: String) {
        try {
            // Create user profile object
            val userProfile = UserProfile(
                uid = userId,
                displayName = name,
                email = email,
                createdAt = System.currentTimeMillis()
            )

            // Save user profile to Firestore
            val result = firestoreService.createUserProfile(userProfile)

            result.onSuccess {
                // Profile created successfully
                onSignUpSuccess()
            }.onFailure { exception ->
                // Profile creation failed, but Firebase account was created
                // You might want to handle this case differently
                onSignUpFailure("Account created but profile setup failed: ${exception.message}")
            }
        } catch (e: Exception) {
            onSignUpFailure("Profile creation failed: ${e.message}")
        } finally {
            showLoading(false)
        }
    }

    private fun onSignUpSuccess() {
        Toast.makeText(this, "Account created successfully! Welcome to FitnessApp!", Toast.LENGTH_LONG).show()

        // Navigate to dashboard
        navigateToDashboard()
    }

    private fun onSignUpFailure(errorMessage: String) {
        showLoading(false)

        // Show error message
        Toast.makeText(this, "Sign up failed: $errorMessage", Toast.LENGTH_LONG).show()

        // Clear password fields for security
        etPassword.text?.clear()
        etConfirmPassword.text?.clear()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, login::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, Dashboard::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            btnSignUp.isEnabled = false
            btnSignUp.text = "Creating Account..."
            try {
                progressIndicator.visibility = View.VISIBLE
            } catch (e: Exception) {
                // Progress indicator not available
            }
        } else {
            btnSignUp.isEnabled = true
            btnSignUp.text = "Sign Up"
            try {
                progressIndicator.visibility = View.GONE
            } catch (e: Exception) {
                // Progress indicator not available
            }
        }
    }

    // Helper method to clear all input fields
    private fun clearFields() {
        etName.text?.clear()
        etPhone.text?.clear()
        etEmail.text?.clear()
        etPassword.text?.clear()
        etConfirmPassword.text?.clear()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is already authenticated
        authService.getCurrentUser()?.let {
            navigateToDashboard()
        }
    }
}