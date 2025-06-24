package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class signup : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnSignUp: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvLogin = findViewById(R.id.tvLogin)
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

    private fun performSignUp() {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        // TODO: Implement your signup logic here
        // This could involve:
        // - API call to your backend
        // - Firebase Authentication
        // - Database operations
        // - Shared preferences for local storage

        // For now, we'll just show a success message
        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()

        // Navigate to main activity or login screen
        // navigateToMainActivity() or navigateToLogin()
    }

    private fun navigateToLogin() {
        // Navigate to login activity
        val intent = Intent(this, login::class.java)
        startActivity(intent)
        finish() // Optional: finish this activity
    }

    private fun navigateToMainActivity() {
        // Navigate to main activity after successful signup
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Helper method to clear all input fields
    private fun clearFields() {
        etName.text?.clear()
        etPhone.text?.clear()
        etEmail.text?.clear()
        etPassword.text?.clear()
        etConfirmPassword.text?.clear()
    }
}