package com.example.fitnessapp

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class LoginActivityTest {

    @Test
    fun `empty email should return false`() {
        // Given
        val validator = LoginValidator()

        // When
        val result = validator.validateEmail("")

        // Then
        assertFalse(result.isValid)
        assertEquals("Email is required", result.errorMessage)
    }

    @Test
    fun `invalid email should return false`() {
        // Given
        val validator = LoginValidator()

        // When
        val result = validator.validateEmail("invalid.email")

        // Then
        assertFalse(result.isValid)
        assertEquals("Please enter a valid email address", result.errorMessage)
    }

    @Test
    fun `valid email should return true`() {
        // Given
        val validator = LoginValidator()

        // When
        val result = validator.validateEmail("test@example.com")

        // Then
        assertTrue(result.isValid)
        assertEquals(null, result.errorMessage)
    }

    @Test
    fun `empty password should return false`() {
        // Given
        val validator = LoginValidator()

        // When
        val result = validator.validatePassword("")

        // Then
        assertFalse(result.isValid)
        assertEquals("Password is required", result.errorMessage)
    }

    @Test
    fun `short password should return false`() {
        // Given
        val validator = LoginValidator()

        // When
        val result = validator.validatePassword("123")

        // Then
        assertFalse(result.isValid)
        assertEquals("Password must be at least 6 characters", result.errorMessage)
    }

    @Test
    fun `valid password should return true`() {
        // Given
        val validator = LoginValidator()

        // When
        val result = validator.validatePassword("password123")

        // Then
        assertTrue(result.isValid)
        assertEquals(null, result.errorMessage)
    }

    @Test
    fun `valid inputs should return true`() {
        // Given
        val validator = LoginValidator()

        // When
        val emailResult = validator.validateEmail("test@example.com")
        val passwordResult = validator.validatePassword("password123")

        // Then
        assertTrue(emailResult.isValid && passwordResult.isValid)
    }
}

// Separate validation logic that can be easily tested
class LoginValidator {

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isEmpty() -> ValidationResult(false, "Email is required")
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                ValidationResult(false, "Please enter a valid email address")
            else -> ValidationResult(true, null)
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isEmpty() -> ValidationResult(false, "Password is required")
            password.length < 6 -> ValidationResult(false, "Password must be at least 6 characters")
            else -> ValidationResult(true, null)
        }
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String?
)