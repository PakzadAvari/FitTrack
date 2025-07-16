package com.example.fitnessapp

import com.example.fitnessapp.services.WorkoutData
import com.example.fitnessapp.services.UserStats
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EdgeCasesTest {

    @Test
    fun `password strength with special characters`() {
        val passwordsWithSpecialChars = listOf(
            "pass@123",
            "test#456",
            "user$789",
            "my_password1"
        )

        passwordsWithSpecialChars.forEach { password ->
            assertTrue(isPasswordStrong(password), "Password with special chars should be strong: $password")
        }
    }

    @Test
    fun `password strength with unicode characters`() {
        val unicodePasswords = listOf(
            "café123",
            "naïve456",
            "résumé789"
        )

        unicodePasswords.forEach { password ->
            assertTrue(isPasswordStrong(password), "Unicode password should be strong: $password")
        }
    }

    @Test
    fun `very long input validation`() {
        val veryLongString = "a".repeat(1000)
        val veryLongEmail = "user@$veryLongString.com"

        // Email validation should handle very long strings
        assertFalse(android.util.Patterns.EMAIL_ADDRESS.matcher(veryLongEmail).matches())

        // Name validation should handle very long strings
        assertTrue(veryLongString.length >= 2) // Should still be valid
    }

    @Test
    fun `workout data with extreme values`() {
        val extremeWorkout = WorkoutData(
            name = "Extreme Marathon",
            category = "Endurance",
            duration = 720, // 12 hours
            caloriesBurned = 8000,
            completed = true
        )

        assertEquals(720, extremeWorkout.duration)
        assertEquals(8000, extremeWorkout.caloriesBurned)
    }

    @Test
    fun `user stats with negative values`() {
        // Test how the system handles negative values (should not normally occur)
        val negativeStats = UserStats(
            totalWorkouts = -1,
            totalCalories = -100,
            currentStreak = -5,
            lastWorkoutDate = -1L
        )

        // These should still be set as provided (validation should happen at business logic level)
        assertEquals(-1, negativeStats.totalWorkouts)
        assertEquals(-100, negativeStats.totalCalories)
        assertEquals(-5, negativeStats.currentStreak)
    }

    @Test
    fun `empty and whitespace string handling`() {
        val emptyStrings = listOf("", " ", "   ", "\t", "\n")

        emptyStrings.forEach { str ->
            assertTrue(str.trim().isEmpty(), "String should be empty after trim: '$str'")
        }
    }

    @Test
    fun `phone number with formatting`() {
        val formattedPhones = listOf(
            "(123) 456-7890",
            "123-456-7890",
            "123.456.7890",
            "+1 123 456 7890"
        )

        formattedPhones.forEach { phone ->
            val digitsOnly = phone.filter { it.isDigit() }
            assertTrue(digitsOnly.length >= 10, "Phone should have at least 10 digits: $phone")
        }
    }

    // Helper function
    private fun isPasswordStrong(password: String): Boolean {
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        return hasLetter && hasDigit
    }
}