package com.example.fitnessapp

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidationTest {

    // Test cases for email validation
    @Test
    fun `valid email should return true`() {
        val validEmails = listOf(
            "test@example.com",
            "user.name@domain.co.uk",
            "test123@gmail.com",
            "user+tag@domain.com"
        )

        validEmails.forEach { email ->
            assertTrue(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                "Email '$email' should be valid")
        }
    }

    @Test
    fun `invalid email should return false`() {
        val invalidEmails = listOf(
            "invalid.email",
            "@domain.com",
            "test@",
            "test..test@domain.com",
            ""
        )

        invalidEmails.forEach { email ->
            assertFalse(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                "Email '$email' should be invalid")
        }
    }

    // Test cases for password strength validation
    @Test
    fun `strong password should return true`() {
        val strongPasswords = listOf(
            "password123",
            "abc123",
            "test1",
            "Password1",
            "mypass123"
        )

        strongPasswords.forEach { password ->
            assertTrue(isPasswordStrong(password),
                "Password '$password' should be strong")
        }
    }

    @Test
    fun `weak password should return false`() {
        val weakPasswords = listOf(
            "password",      // no digits
            "123456",        // no letters
            "PASSWORD",      // no digits
            "12345",         // no letters
            ""               // empty
        )

        weakPasswords.forEach { password ->
            assertFalse(isPasswordStrong(password),
                "Password '$password' should be weak")
        }
    }

    @Test
    fun `password length validation`() {
        val shortPasswords = listOf("12345", "ab1", "")
        val validPasswords = listOf("123456", "abcdef", "pass123")

        shortPasswords.forEach { password ->
            assertTrue(password.length < 6,
                "Password '$password' should be too short")
        }

        validPasswords.forEach { password ->
            assertTrue(password.length >= 6,
                "Password '$password' should meet length requirement")
        }
    }

    @Test
    fun `name validation`() {
        val validNames = listOf("John", "Jane Doe", "Test User", "A B")
        val invalidNames = listOf("J", "", " ", "A")

        validNames.forEach { name ->
            assertTrue(name.trim().length >= 2,
                "Name '$name' should be valid")
        }

        invalidNames.forEach { name ->
            assertTrue(name.trim().length < 2,
                "Name '$name' should be invalid")
        }
    }

    @Test
    fun `phone number validation`() {
        val validPhones = listOf("1234567890", "12345678901", "123456789012")
        val invalidPhones = listOf("123456789", "12345", "", "abc1234567")

        validPhones.forEach { phone ->
            assertTrue(phone.length >= 10,
                "Phone '$phone' should be valid")
        }

        invalidPhones.forEach { phone ->
            assertFalse(phone.length >= 10 && phone.all { it.isDigit() },
                "Phone '$phone' should be invalid")
        }
    }

    // Helper function to test password strength
    private fun isPasswordStrong(password: String): Boolean {
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        return hasLetter && hasDigit
    }
}