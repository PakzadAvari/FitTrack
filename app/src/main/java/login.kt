package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class login : AppCompatActivity() {

    // Firebase Services
    private lateinit var authService: FirebaseAuthService
    private lateinit var firestoreService: FirestoreService

    // UI Components
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var signupText: android.widget.TextView
    private lateinit var progressIndicator: CircularProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase services
        initializeFirebase()

        // Check if user is already logged in
        if (authService.getCurrentUser() != null) {
            navigateToDashboard()
            return
        }

        // Initialize views
        initViews()

        // Set click listeners
        setupClickListeners()
    }

    private fun initializeFirebase() {
        authService = FirebaseAuthService()
        firestoreService = FirestoreService()
    }

    private fun initViews() {
        // Find views by ID
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signupText = findViewById(R.id.signupText)

        // Add progress indicator if not in layout
        try {
            progressIndicator = findViewById(R.id.progressIndicator)
        } catch (e: Exception) {
            // Progress indicator not found in layout, create programmatically if needed
        }
    }

    private fun setupClickListeners() {
        // Login button click listener
        loginButton.setOnClickListener {
            performLogin()
        }

        // Signup text click listener
        signupText.setOnClickListener {
            navigateToSignup()
        }

        // Clear error when user starts typing
        emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                emailInputLayout.error = null
            }
        }

        passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                passwordInputLayout.error = null
            }
        }
    }

    private fun performLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Reset previous errors
        emailInputLayout.error = null
        passwordInputLayout.error = null

        // Validate inputs
        if (!validateInputs(email, password)) {
            return
        }

        // Show loading state
        showLoading(true)

        // Perform Firebase authentication
        lifecycleScope.launch {
            try {
                val result = authService.signInWithEmailAndPassword(email, password)

                result.onSuccess { user ->
                    // Login successful
                    onLoginSuccess(user.uid)
                }.onFailure { exception ->
                    // Login failed
                    onLoginFailure(exception.message ?: "Login failed")
                }
            } catch (e: Exception) {
                onLoginFailure(e.message ?: "An unexpected error occurred")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        // Validate email
        if (TextUtils.isEmpty(email)) {
            emailInputLayout.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = "Please enter a valid email address"
            isValid = false
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordInputLayout.error = "Password must be at least 6 characters"
            isValid = false
        }

        return isValid
    }

    private fun onLoginSuccess(userId: String) {
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

        // Navigate to dashboard
        navigateToDashboard()
    }

    private fun onLoginFailure(errorMessage: String) {
        Toast.makeText(this, "Login failed: $errorMessage", Toast.LENGTH_LONG).show()

        // Clear password field for security
        passwordEditText.text?.clear()
        passwordEditText.requestFocus()
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, Dashboard::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToSignup() {
        val intent = Intent(this, signup::class.java)
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            loginButton.isEnabled = false
            loginButton.text = "Logging in..."
            try {
                progressIndicator.visibility = View.VISIBLE
            } catch (e: Exception) {
                // Progress indicator not available
            }
        } else {
            loginButton.isEnabled = true
            loginButton.text = "Login"
            try {
                progressIndicator.visibility = View.GONE
            } catch (e: Exception) {
                // Progress indicator not available
            }
        }
    }

    // Handle back button press
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // Close the app when back is pressed on login
    }

    override fun onStart() {
        super.onStart()
        // Check if user is already authenticated
        authService.getCurrentUser()?.let {
            navigateToDashboard()
        }
    }
}