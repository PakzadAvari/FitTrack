package com.example.fitnessapp


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class login : AppCompatActivity() {

    // UI Components
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var signupText: android.widget.TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        initViews()

        // Set click listeners
        setupClickListeners()
    }

    private fun initViews() {
        // Find views by ID
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signupText = findViewById(R.id.signupText)
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
        loginButton.isEnabled = false
        loginButton.text = "Logging in..."

        // Simulate login process (replace with actual authentication logic)
        simulateLogin(email, password)
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

    private fun simulateLogin(email: String, password: String) {
        // Simulate network delay
        android.os.Handler(mainLooper).postDelayed({
            // Reset button state
            loginButton.isEnabled = true
            loginButton.text = "Login"

            // For demo purposes, accept any valid email/password combination
            // Replace this with actual authentication logic
            if (isValidCredentials(email, password)) {
                onLoginSuccess()
            } else {
                onLoginFailure()
            }
        }, 2000) // 2 second delay to simulate network request
    }

    private fun isValidCredentials(email: String, password: String): Boolean {
        // Demo logic - replace with actual authentication
        // For demo, accept any email with password "password123"
        return password == "password123"
    }

    private fun onLoginSuccess() {
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

        // Navigate to main activity or dashboard
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun onLoginFailure() {
        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_LONG).show()

        // Clear password field for security
        passwordEditText.text?.clear()
        passwordEditText.requestFocus()
    }

    private fun navigateToSignup() {
        // Navigate to signup activity
        val intent = Intent(this, signup::class.java)
        startActivity(intent)
    }

    // Handle back button press
    override fun onBackPressed() {
        super.onBackPressed()
        // You can add custom back button behavior here if needed
    }

    // Save user preferences or handle activity lifecycle
    override fun onPause() {
        super.onPause()
        // Save any necessary data
    }

    override fun onResume() {
        super.onResume()
        // Restore any necessary data or refresh UI
    }
}