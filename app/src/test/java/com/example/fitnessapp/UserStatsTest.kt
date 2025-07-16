package com.example.fitnessapp.services

import org.junit.Test
import kotlin.test.assertEquals

class UserStatsTest {

    @Test
    fun `UserStats default values should be correct`() {
        // Given
        val stats = UserStats()

        // Then
        assertEquals(0, stats.totalWorkouts)
        assertEquals(0, stats.totalCalories)
        assertEquals(0, stats.currentStreak)
        assertEquals(0, stats.lastWorkoutDate)
    }

    @Test
    fun `UserStats with custom values should be correct`() {
        // Given
        val expectedWorkouts = 10
        val expectedCalories = 1000
        val expectedStreak = 5
        val expectedDate = System.currentTimeMillis()

        // When
        val stats = UserStats(
            totalWorkouts = expectedWorkouts,
            totalCalories = expectedCalories,
            currentStreak = expectedStreak,
            lastWorkoutDate = expectedDate
        )

        // Then
        assertEquals(expectedWorkouts, stats.totalWorkouts)
        assertEquals(expectedCalories, stats.totalCalories)
        assertEquals(expectedStreak, stats.currentStreak)
        assertEquals(expectedDate, stats.lastWorkoutDate)
    }

    @Test
    fun `UserStats should handle large numbers correctly`() {
        // Given
        val largeWorkouts = 1000
        val largeCalories = 100000
        val largeStreak = 365

        // When
        val stats = UserStats(
            totalWorkouts = largeWorkouts,
            totalCalories = largeCalories,
            currentStreak = largeStreak
        )

        // Then
        assertEquals(largeWorkouts, stats.totalWorkouts)
        assertEquals(largeCalories, stats.totalCalories)
        assertEquals(largeStreak, stats.currentStreak)
    }
}