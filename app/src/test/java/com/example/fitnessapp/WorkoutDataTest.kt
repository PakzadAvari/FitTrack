package com.example.fitnessapp.services

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WorkoutDataTest {

    @Test
    fun `WorkoutData default values should be correct`() {
        // Given
        val workout = WorkoutData()

        // Then
        assertEquals("", workout.id)
        assertEquals("", workout.name)
        assertEquals("", workout.category)
        assertEquals(0, workout.duration)
        assertEquals(0, workout.caloriesBurned)
        assertFalse(workout.completed)
        assertTrue(workout.date > 0) // Should have a timestamp
    }

    @Test
    fun `WorkoutData with custom values should be correct`() {
        // Given
        val expectedId = "workout123"
        val expectedName = "Running"
        val expectedCategory = "Cardio"
        val expectedDuration = 30
        val expectedCalories = 200
        val expectedDate = System.currentTimeMillis()
        val expectedCompleted = true

        // When
        val workout = WorkoutData(
            id = expectedId,
            name = expectedName,
            category = expectedCategory,
            duration = expectedDuration,
            caloriesBurned = expectedCalories,
            date = expectedDate,
            completed = expectedCompleted
        )

        // Then
        assertEquals(expectedId, workout.id)
        assertEquals(expectedName, workout.name)
        assertEquals(expectedCategory, workout.category)
        assertEquals(expectedDuration, workout.duration)
        assertEquals(expectedCalories, workout.caloriesBurned)
        assertEquals(expectedDate, workout.date)
        assertEquals(expectedCompleted, workout.completed)
    }
}