package com.example.fitnessapp

object TestUtils {

    // Test data constants
    const val VALID_EMAIL = "test@example.com"
    const val VALID_PASSWORD = "password123"
    const val VALID_NAME = "John Doe"
    const val VALID_PHONE = "1234567890"
    const val USER_ID = "test_user_123"

    // Invalid test data
    const val INVALID_EMAIL = "invalid.email"
    const val SHORT_PASSWORD = "123"
    const val WEAK_PASSWORD = "password"
    const val SHORT_NAME = "J"
    const val SHORT_PHONE = "123"

    // Create test workout data
    fun createTestWorkout(
        id: String = "workout123",
        name: String = "Running",
        category: String = "Cardio",
        duration: Int = 30,
        calories: Int = 200,
        completed: Boolean = true
    ) = com.example.fitnessapp.services.WorkoutData(
        id = id,
        name = name,
        category = category,
        duration = duration,
        caloriesBurned = calories,
        date = System.currentTimeMillis(),
        completed = completed
    )

    // Create test user stats
    fun createTestUserStats(
        totalWorkouts: Int = 10,
        totalCalories: Int = 1000,
        currentStreak: Int = 5,
        lastWorkoutDate: Long = System.currentTimeMillis()
    ) = com.example.fitnessapp.services.UserStats(
        totalWorkouts = totalWorkouts,
        totalCalories = totalCalories,
        currentStreak = currentStreak,
        lastWorkoutDate = lastWorkoutDate
    )
}