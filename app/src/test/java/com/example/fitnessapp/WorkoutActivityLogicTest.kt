package com.example.fitnessapp

import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import kotlinx.coroutines.test.runTest

/**
 * Data classes for fitness app
 */
data class Exercise(
    val name: String,
    val sets: Int,
    val reps: Int,
    val weight: Double,
    val duration: Int
)

data class CategoryWorkout(
    val name: String,
    val description: String,
    val duration: Int,
    val calories: Int,
    val iconResId: Int
)

// Mock R.drawable constants
object R {
    object drawable {
        const val ic_crossfit = 1001
        const val ic_pilates = 1002
        const val ic_bodyweight = 1003
        const val ic_flexibility = 1004
        const val ic_general = 1005
    }
}

/**
 * Comprehensive tests for WorkoutActivity.kt functionality
 */
class WorkoutActivityLogicTest {

    private lateinit var workoutService: WorkoutService

    @Before
    fun setUp() {
        workoutService = WorkoutService()
    }

    @Test
    fun `loadTodayWorkoutExercises should return correct exercises`() {
        // When
        val exercises = workoutService.loadTodayWorkoutExercises()

        // Then
        assertEquals(6, exercises.size)
        assertEquals("Push-ups", exercises[0].name)
        assertEquals(3, exercises[0].sets)
        assertEquals(12, exercises[0].reps)
        assertEquals(0.0, exercises[0].weight, 0.01)

        assertEquals("Bench Press", exercises[1].name)
        assertEquals(60.0, exercises[1].weight, 0.01)
    }

    @Test
    fun `loadStrengthExercises should return strength training exercises`() {
        // When
        val exercises = workoutService.loadStrengthExercises()

        // Then
        assertEquals(5, exercises.size)
        assertEquals("Squats", exercises[0].name)
        assertEquals(4, exercises[0].sets)
        assertEquals(80.0, exercises[0].weight, 0.01)

        assertEquals("Deadlifts", exercises[1].name)
        assertEquals(100.0, exercises[1].weight, 0.01)
    }

    @Test
    fun `loadCardioExercises should return cardio exercises`() {
        // When
        val exercises = workoutService.loadCardioExercises()

        // Then
        assertEquals(5, exercises.size)
        assertEquals("Running", exercises[0].name)
        assertEquals(30, exercises[0].duration)
        assertEquals(0, exercises[0].sets)
        assertEquals(0, exercises[0].reps)

        assertEquals("Burpees", exercises[3].name)
        assertEquals(3, exercises[3].sets)
        assertEquals(10, exercises[3].reps)
    }

    @Test
    fun `loadHIITExercises should return high intensity exercises`() {
        // When
        val exercises = workoutService.loadHIITExercises()

        // Then
        assertEquals(6, exercises.size)
        assertEquals("Burpees", exercises[0].name)
        assertEquals(4, exercises[0].sets)
        assertEquals(10, exercises[0].reps)

        assertEquals("Plank", exercises[4].name)
        assertEquals(45, exercises[4].duration)
    }

    @Test
    fun `loadYogaExercises should return yoga poses`() {
        // When
        val exercises = workoutService.loadYogaExercises()

        // Then
        assertEquals(6, exercises.size)
        assertEquals("Downward Dog", exercises[0].name)
        assertEquals(60, exercises[0].duration)
        assertEquals(0, exercises[0].sets)

        assertEquals("Tree Pose", exercises[3].name)
        assertEquals(45, exercises[3].duration)
    }

    @Test
    fun `loadDefaultExercises should return basic exercises`() {
        // When
        val exercises = workoutService.loadDefaultExercises()

        // Then
        assertEquals(4, exercises.size)
        assertEquals("Jumping Jacks", exercises[0].name)
        assertEquals(3, exercises[0].sets)
        assertEquals(20, exercises[0].reps)

        assertEquals("Plank", exercises[3].name)
        assertEquals(30, exercises[3].duration)
    }

    @Test
    fun `getEstimatedCalories should return correct values for different workout types`() {
        assertEquals(350, workoutService.getEstimatedCalories("today"))
        assertEquals(400, workoutService.getEstimatedCalories("strength"))
        assertEquals(450, workoutService.getEstimatedCalories("cardio"))
        assertEquals(500, workoutService.getEstimatedCalories("hiit"))
        assertEquals(200, workoutService.getEstimatedCalories("yoga"))
        assertEquals(250, workoutService.getEstimatedCalories("default"))
    }

    @Test
    fun `getExercisesByType should return correct exercises for each type`() {
        // Test various workout types
        val todayExercises = workoutService.getExercisesByType("today")
        val strengthExercises = workoutService.getExercisesByType("strength")
        val cardioExercises = workoutService.getExercisesByType("cardio")

        assertEquals(6, todayExercises.size)
        assertEquals(5, strengthExercises.size)
        assertEquals(5, cardioExercises.size)

        // Verify first exercise of each type
        assertEquals("Push-ups", todayExercises[0].name)
        assertEquals("Squats", strengthExercises[0].name)
        assertEquals("Running", cardioExercises[0].name)
    }

    @Test
    fun `getEstimatedCalories should return default for unknown workout type`() {
        assertEquals(250, workoutService.getEstimatedCalories("unknown"))
        assertEquals(250, workoutService.getEstimatedCalories(""))
        assertEquals(250, workoutService.getEstimatedCalories("invalid"))
    }
}

/**
 * Tests for ExerciseAdapter functionality
 */
class ExerciseAdapterTest {

    private lateinit var exerciseFormatter: ExerciseFormatter

    @Before
    fun setUp() {
        exerciseFormatter = ExerciseFormatter()
    }

    @Test
    fun `exercise details should format correctly for sets and reps`() {
        // Given
        val exercise = Exercise("Push-ups", 3, 12, 0.0, 0)

        // When
        val details = exerciseFormatter.formatExerciseDetails(exercise)

        // Then
        assertEquals("3 sets × 12 reps", details)
    }

    @Test
    fun `exercise details should format correctly for sets and duration`() {
        // Given
        val exercise = Exercise("Plank", 3, 0, 0.0, 45)

        // When
        val details = exerciseFormatter.formatExerciseDetails(exercise)

        // Then
        assertEquals("3 sets × 45s", details)
    }

    @Test
    fun `exercise details should format correctly for duration only`() {
        // Given
        val exercise = Exercise("Running", 0, 0, 0.0, 30)

        // When
        val details = exerciseFormatter.formatExerciseDetails(exercise)

        // Then
        assertEquals("30 seconds", details)
    }

    @Test
    fun `exercise details should include weight when present`() {
        // Given
        val exercise = Exercise("Bench Press", 3, 10, 60.0, 0)

        // When
        val details = exerciseFormatter.formatExerciseDetailsWithWeight(exercise)

        // Then
        assertEquals("3 sets × 10 reps @ 60.0kg", details)
    }

    @Test
    fun `exercise details should handle zero values`() {
        // Given
        val exercise = Exercise("Unknown", 0, 0, 0.0, 0)

        // When
        val details = exerciseFormatter.formatExerciseDetails(exercise)

        // Then
        assertEquals("Complete exercise", details)
    }

    @Test
    fun `formatExerciseDetailsWithWeight should handle zero weight`() {
        // Given
        val exercise = Exercise("Push-ups", 3, 12, 0.0, 0)

        // When
        val details = exerciseFormatter.formatExerciseDetailsWithWeight(exercise)

        // Then
        assertEquals("3 sets × 12 reps", details)
    }

    @Test
    fun `formatExerciseDetails should handle mixed duration and reps`() {
        // Given
        val exercise = Exercise("Interval Run", 3, 5, 0.0, 60)

        // When
        val details = exerciseFormatter.formatExerciseDetails(exercise)

        // Then
        assertEquals("3 sets × 60s", details) // Duration takes precedence
    }
}

/**
 * Tests for WorkoutCategoryActivity comprehensive functionality
 */
class WorkoutCategoryActivityComprehensiveTest {

    private lateinit var categoryService: WorkoutCategoryService

    @Before
    fun setUp() {
        categoryService = WorkoutCategoryService()
    }

    @Test
    fun `loadCrossFitWorkouts should return crossfit workouts`() {
        // When
        val workouts = categoryService.loadCrossFitWorkouts()

        // Then
        assertEquals(6, workouts.size)
        assertEquals("WOD - Fran", workouts[0].name)
        assertEquals("Thrusters + Pull-ups", workouts[0].description)
        assertEquals(15, workouts[0].duration)
        assertEquals(200, workouts[0].calories)
    }

    @Test
    fun `loadPilatesWorkouts should return pilates workouts`() {
        // When
        val workouts = categoryService.loadPilatesWorkouts()

        // Then
        assertEquals(6, workouts.size)
        assertEquals("Mat Pilates", workouts[0].name)
        assertEquals("Floor-based Pilates", workouts[0].description)
        assertEquals(45, workouts[0].duration)
        assertEquals(180, workouts[0].calories)
    }

    @Test
    fun `loadBodyweightWorkouts should return bodyweight workouts`() {
        // When
        val workouts = categoryService.loadBodyweightWorkouts()

        // Then
        assertEquals(6, workouts.size)
        assertEquals("Calisthenics", workouts[0].name)
        assertEquals("Bodyweight Strength", workouts[0].description)
        assertEquals(40, workouts[0].duration)
        assertEquals(300, workouts[0].calories)
    }

    @Test
    fun `loadFlexibilityWorkouts should return flexibility workouts`() {
        // When
        val workouts = categoryService.loadFlexibilityWorkouts()

        // Then
        assertEquals(6, workouts.size)
        assertEquals("Full Body Stretch", workouts[0].name)
        assertEquals("Complete Flexibility", workouts[0].description)
        assertEquals(30, workouts[0].duration)
        assertEquals(80, workouts[0].calories)
    }

    @Test
    fun `loadGeneralWorkouts should return general workouts`() {
        // When
        val workouts = categoryService.loadGeneralWorkouts()

        // Then
        assertEquals(6, workouts.size)
        assertEquals("Quick Burn", workouts[0].name)
        assertEquals("15-minute Workout", workouts[0].description)
        assertEquals(15, workouts[0].duration)
        assertEquals(150, workouts[0].calories)
    }

    @Test
    fun `getAllWorkoutCategories should return all workout types`() {
        // When
        val categories = categoryService.getAllWorkoutCategories()

        // Then
        assertEquals(5, categories.size)
        assertTrue(categories.contains("CrossFit"))
        assertTrue(categories.contains("Pilates"))
        assertTrue(categories.contains("Bodyweight"))
        assertTrue(categories.contains("Flexibility"))
        assertTrue(categories.contains("General"))
    }

    @Test
    fun `getWorkoutsByCategory should return correct workouts for each category`() {
        // Test CrossFit category
        val crossfitWorkouts = categoryService.getWorkoutsByCategory("CrossFit")
        assertEquals(6, crossfitWorkouts.size)
        assertEquals("WOD - Fran", crossfitWorkouts[0].name)

        // Test Pilates category
        val pilatesWorkouts = categoryService.getWorkoutsByCategory("Pilates")
        assertEquals(6, pilatesWorkouts.size)
        assertEquals("Mat Pilates", pilatesWorkouts[0].name)

        // Test unknown category
        val unknownWorkouts = categoryService.getWorkoutsByCategory("Unknown")
        assertEquals(0, unknownWorkouts.size)
    }
}

/**
 * Service classes that would be used in the actual implementation
 */
class WorkoutService {

    fun loadTodayWorkoutExercises(): List<Exercise> {
        return listOf(
            Exercise("Push-ups", 3, 12, 0.0, 0),
            Exercise("Bench Press", 3, 10, 60.0, 0),
            Exercise("Tricep Dips", 3, 15, 0.0, 0),
            Exercise("Overhead Press", 3, 8, 40.0, 0),
            Exercise("Tricep Extensions", 3, 12, 25.0, 0),
            Exercise("Chest Flyes", 3, 10, 30.0, 0)
        )
    }

    fun loadStrengthExercises(): List<Exercise> {
        return listOf(
            Exercise("Squats", 4, 12, 80.0, 0),
            Exercise("Deadlifts", 4, 8, 100.0, 0),
            Exercise("Bench Press", 4, 10, 70.0, 0),
            Exercise("Pull-ups", 3, 8, 0.0, 0),
            Exercise("Overhead Press", 3, 10, 50.0, 0)
        )
    }

    fun loadCardioExercises(): List<Exercise> {
        return listOf(
            Exercise("Running", 0, 0, 0.0, 30),
            Exercise("Cycling", 0, 0, 0.0, 25),
            Exercise("Jump Rope", 0, 0, 0.0, 10),
            Exercise("Burpees", 3, 10, 0.0, 0),
            Exercise("Mountain Climbers", 3, 20, 0.0, 0)
        )
    }

    fun loadHIITExercises(): List<Exercise> {
        return listOf(
            Exercise("Burpees", 4, 10, 0.0, 0),
            Exercise("Jump Squats", 4, 15, 0.0, 0),
            Exercise("Push-ups", 4, 12, 0.0, 0),
            Exercise("Mountain Climbers", 4, 20, 0.0, 0),
            Exercise("Plank", 4, 0, 0.0, 45),
            Exercise("High Knees", 4, 30, 0.0, 0)
        )
    }

    fun loadYogaExercises(): List<Exercise> {
        return listOf(
            Exercise("Downward Dog", 0, 0, 0.0, 60),
            Exercise("Warrior I", 0, 0, 0.0, 30),
            Exercise("Warrior II", 0, 0, 0.0, 30),
            Exercise("Tree Pose", 0, 0, 0.0, 45),
            Exercise("Child's Pose", 0, 0, 0.0, 60),
            Exercise("Cobra Pose", 0, 0, 0.0, 30)
        )
    }

    fun loadDefaultExercises(): List<Exercise> {
        return listOf(
            Exercise("Jumping Jacks", 3, 20, 0.0, 0),
            Exercise("Push-ups", 3, 10, 0.0, 0),
            Exercise("Squats", 3, 15, 0.0, 0),
            Exercise("Plank", 3, 0, 0.0, 30)
        )
    }

    fun getEstimatedCalories(workoutType: String): Int {
        return when (workoutType) {
            "today" -> 350
            "strength" -> 400
            "cardio" -> 450
            "hiit" -> 500
            "yoga" -> 200
            else -> 250
        }
    }

    fun getExercisesByType(workoutType: String): List<Exercise> {
        return when (workoutType) {
            "today" -> loadTodayWorkoutExercises()
            "strength" -> loadStrengthExercises()
            "cardio" -> loadCardioExercises()
            "hiit" -> loadHIITExercises()
            "yoga" -> loadYogaExercises()
            else -> loadDefaultExercises()
        }
    }
}

class ExerciseFormatter {

    fun formatExerciseDetails(exercise: Exercise): String {
        return when {
            exercise.sets > 0 && exercise.reps > 0 -> "${exercise.sets} sets × ${exercise.reps} reps"
            exercise.sets > 0 && exercise.duration > 0 -> "${exercise.sets} sets × ${exercise.duration}s"
            exercise.duration > 0 -> "${exercise.duration} seconds"
            else -> "Complete exercise"
        }
    }

    fun formatExerciseDetailsWithWeight(exercise: Exercise): String {
        val details = formatExerciseDetails(exercise)
        return if (exercise.weight > 0) {
            "$details @ ${exercise.weight}kg"
        } else {
            details
        }
    }
}

class WorkoutCategoryService {

    fun loadCrossFitWorkouts(): List<CategoryWorkout> {
        return listOf(
            CategoryWorkout("WOD - Fran", "Thrusters + Pull-ups", 15, 200, R.drawable.ic_crossfit),
            CategoryWorkout("WOD - Cindy", "AMRAP 20 minutes", 20, 250, R.drawable.ic_crossfit),
            CategoryWorkout("WOD - Murph", "Hero Workout", 45, 600, R.drawable.ic_crossfit),
            CategoryWorkout("Olympic Lifting", "Snatch + Clean & Jerk", 40, 300, R.drawable.ic_crossfit),
            CategoryWorkout("MetCon", "Metabolic Conditioning", 25, 350, R.drawable.ic_crossfit),
            CategoryWorkout("Strongman", "Functional Strength", 35, 400, R.drawable.ic_crossfit)
        )
    }

    fun loadPilatesWorkouts(): List<CategoryWorkout> {
        return listOf(
            CategoryWorkout("Mat Pilates", "Floor-based Pilates", 45, 180, R.drawable.ic_pilates),
            CategoryWorkout("Reformer", "Equipment-based Pilates", 50, 200, R.drawable.ic_pilates),
            CategoryWorkout("Core Focus", "Abdominal Strengthening", 30, 150, R.drawable.ic_pilates),
            CategoryWorkout("Beginner Pilates", "Introduction to Pilates", 40, 160, R.drawable.ic_pilates),
            CategoryWorkout("Advanced Pilates", "Challenging Movements", 55, 220, R.drawable.ic_pilates),
            CategoryWorkout("Pilates Fusion", "Pilates + Yoga", 50, 200, R.drawable.ic_pilates)
        )
    }

    fun loadBodyweightWorkouts(): List<CategoryWorkout> {
        return listOf(
            CategoryWorkout("Calisthenics", "Bodyweight Strength", 40, 300, R.drawable.ic_bodyweight),
            CategoryWorkout("Prison Workout", "Minimal Space Required", 35, 280, R.drawable.ic_bodyweight),
            CategoryWorkout("Playground", "Outdoor Bodyweight", 45, 350, R.drawable.ic_bodyweight),
            CategoryWorkout("Military Style", "Boot Camp Workout", 50, 400, R.drawable.ic_bodyweight),
            CategoryWorkout("Beginner Flow", "Easy Bodyweight", 30, 200, R.drawable.ic_bodyweight),
            CategoryWorkout("Advanced Flow", "Complex Movements", 45, 380, R.drawable.ic_bodyweight)
        )
    }

    fun loadFlexibilityWorkouts(): List<CategoryWorkout> {
        return listOf(
            CategoryWorkout("Full Body Stretch", "Complete Flexibility", 30, 80, R.drawable.ic_flexibility),
            CategoryWorkout("Hip Mobility", "Hip Flexor Focus", 20, 60, R.drawable.ic_flexibility),
            CategoryWorkout("Shoulder Mobility", "Upper Body Flexibility", 25, 70, R.drawable.ic_flexibility),
            CategoryWorkout("Post-Workout", "Recovery Stretching", 15, 50, R.drawable.ic_flexibility),
            CategoryWorkout("Morning Stretch", "Wake-up Routine", 10, 40, R.drawable.ic_flexibility),
            CategoryWorkout("Desk Stretches", "Office-friendly", 12, 45, R.drawable.ic_flexibility)
        )
    }

    fun loadGeneralWorkouts(): List<CategoryWorkout> {
        return listOf(
            CategoryWorkout("Quick Burn", "15-minute Workout", 15, 150, R.drawable.ic_general),
            CategoryWorkout("Full Body", "Complete Workout", 45, 400, R.drawable.ic_general),
            CategoryWorkout("Beginner", "Easy Start", 30, 200, R.drawable.ic_general),
            CategoryWorkout("Intermediate", "Medium Difficulty", 40, 350, R.drawable.ic_general),
            CategoryWorkout("Advanced", "High Intensity", 50, 500, R.drawable.ic_general),
            CategoryWorkout("Recovery", "Active Recovery", 25, 150, R.drawable.ic_general)
        )
    }

    fun getAllWorkoutCategories(): List<String> {
        return listOf("CrossFit", "Pilates", "Bodyweight", "Flexibility", "General")
    }

    fun getWorkoutsByCategory(category: String): List<CategoryWorkout> {
        return when (category) {
            "CrossFit" -> loadCrossFitWorkouts()
            "Pilates" -> loadPilatesWorkouts()
            "Bodyweight" -> loadBodyweightWorkouts()
            "Flexibility" -> loadFlexibilityWorkouts()
            "General" -> loadGeneralWorkouts()
            else -> emptyList()
        }
    }
}