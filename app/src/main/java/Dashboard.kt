// First, add these dependencies to your app/build.gradle file:
/*
dependencies {
    implementation 'com.google.firebase:firebase-auth-ktx:22.3.0'
    implementation 'com.google.firebase:firebase-firestore-ktx:24.9.1'
    implementation 'com.google.firebase:firebase-analytics-ktx:21.5.0'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
}
*/

package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

// Firebase imports
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.Timestamp

// Data classes
data class UserStats(
    val totalWorkouts: Int = 0,
    val totalCalories: Int = 0,
    val lastWorkoutDate: Long = 0L,
    val joinDate: Long = System.currentTimeMillis(),
    val weeklyGoal: Int = 5,
    val currentStreak: Int = 0
)

data class WorkoutData(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val duration: Int = 0,
    val calories: Int = 0,
    val date: Long = System.currentTimeMillis(),
    val exercises: List<Exercise> = emptyList()
)

data class Exercise(
    val name: String = "",
    val sets: Int = 0,
    val reps: Int = 0,
    val weight: Double = 0.0,
    val duration: Int = 0 // for cardio exercises
)

data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val age: Int = 0,
    val height: Double = 0.0,
    val weight: Double = 0.0,
    val fitnessLevel: String = "Beginner",
    val goals: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

// Firebase Authentication Service
class FirebaseAuthService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                Result.success(user)
            } ?: Result.failure(Exception("Sign in failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                Result.success(user)
            } ?: Result.failure(Exception("Account creation failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.addAuthStateListener(listener)
    }

    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.removeAuthStateListener(listener)
    }
}

// Firestore Database Service
class FirestoreService {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        private const val TAG = "FirestoreService"
        private const val USERS_COLLECTION = "users"
        private const val WORKOUTS_COLLECTION = "workouts"
        private const val USER_STATS_COLLECTION = "userStats"
    }

    // User Profile Operations
    suspend fun createUserProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            db.collection(USERS_COLLECTION)
                .document(userProfile.uid)
                .set(userProfile)
                .await()

            // Initialize user stats
            val initialStats = UserStats()
            db.collection(USER_STATS_COLLECTION)
                .document(userProfile.uid)
                .set(initialStats)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating user profile", e)
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(userId: String): Result<UserProfile> {
        return try {
            val document = db.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                val profile = document.toObject(UserProfile::class.java)
                profile?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Failed to parse user profile"))
            } else {
                Result.failure(Exception("User profile not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user profile", e)
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(userId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            db.collection(USERS_COLLECTION)
                .document(userId)
                .update(updates)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user profile", e)
            Result.failure(e)
        }
    }

    // User Stats Operations
    suspend fun getUserStats(userId: String): Result<UserStats> {
        return try {
            val document = db.collection(USER_STATS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                val stats = document.toObject(UserStats::class.java)
                stats?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Failed to parse user stats"))
            } else {
                // Create initial stats if they don't exist
                val initialStats = UserStats()
                db.collection(USER_STATS_COLLECTION)
                    .document(userId)
                    .set(initialStats)
                    .await()
                Result.success(initialStats)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user stats", e)
            Result.failure(e)
        }
    }

    suspend fun updateUserStats(userId: String, stats: UserStats): Result<Unit> {
        return try {
            db.collection(USER_STATS_COLLECTION)
                .document(userId)
                .set(stats, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user stats", e)
            Result.failure(e)
        }
    }

    // Workout Operations
    suspend fun saveWorkout(userId: String, workout: WorkoutData): Result<String> {
        return try {
            val workoutRef = db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(WORKOUTS_COLLECTION)
                .document()

            val workoutWithId = workout.copy(id = workoutRef.id)
            workoutRef.set(workoutWithId).await()

            Result.success(workoutRef.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving workout", e)
            Result.failure(e)
        }
    }

    suspend fun getUserWorkouts(userId: String, limit: Int = 50): Result<List<WorkoutData>> {
        return try {
            val documents = db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(WORKOUTS_COLLECTION)
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val workouts = documents.toObjects(WorkoutData::class.java)
            Result.success(workouts)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user workouts", e)
            Result.failure(e)
        }
    }

    suspend fun getWorkoutsByDateRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Result<List<WorkoutData>> {
        return try {
            val documents = db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(WORKOUTS_COLLECTION)
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val workouts = documents.toObjects(WorkoutData::class.java)
            Result.success(workouts)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting workouts by date range", e)
            Result.failure(e)
        }
    }

    suspend fun deleteWorkout(userId: String, workoutId: String): Result<Unit> {
        return try {
            db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(WORKOUTS_COLLECTION)
                .document(workoutId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting workout", e)
            Result.failure(e)
        }
    }

    // Analytics and Progress Tracking
    suspend fun getWeeklyStats(userId: String): Result<Map<String, Int>> {
        return try {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val weekStart = calendar.timeInMillis

            calendar.add(Calendar.WEEK_OF_YEAR, 1)
            val weekEnd = calendar.timeInMillis

            val workoutsResult = getWorkoutsByDateRange(userId, weekStart, weekEnd)
            workoutsResult.fold(
                onSuccess = { workouts ->
                    val stats = mutableMapOf<String, Int>()
                    stats["totalWorkouts"] = workouts.size
                    stats["totalCalories"] = workouts.sumOf { it.calories }
                    stats["totalDuration"] = workouts.sumOf { it.duration }

                    val workoutTypes = workouts.groupBy { it.type }
                    workoutTypes.forEach { (type, typeWorkouts) ->
                        stats["${type.lowercase()}_count"] = typeWorkouts.size
                    }

                    Result.success(stats)
                },
                onFailure = { exception ->
                    Result.failure(exception)
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting weekly stats", e)
            Result.failure(e)
        }
    }
}

class Dashboard : AppCompatActivity() {

    // Firebase Services
    private lateinit var authService: FirebaseAuthService
    private lateinit var firestoreService: FirestoreService
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    // User data
    private lateinit var userStats: UserStats
    private var userProfile: UserProfile? = null
    private var userName = "Champion"
    private var todayWorkout = "Push Day - Chest & Triceps"

    // UI Components
    private lateinit var greetingText: TextView
    private lateinit var userNameText: TextView
    private lateinit var workoutCountText: TextView
    private lateinit var caloriesText: TextView
    private lateinit var todayWorkoutTitle: TextView
    private lateinit var todayWorkoutDesc: TextView
    private lateinit var startWorkoutBtn: Button
    private lateinit var progressBtn: Button
    private lateinit var scheduleBtn: Button
    private lateinit var profileImage: ImageView

    // Workout Category Cards
    private lateinit var strengthCard: CardView
    private lateinit var cardioCard: CardView
    private lateinit var hiitCard: CardView
    private lateinit var yogaCard: CardView
    private lateinit var crossfitCard: CardView
    private lateinit var pilatesCard: CardView
    private lateinit var bodyweightCard: CardView
    private lateinit var flexibilityCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        // Initialize Firebase services
        initializeFirebase()

        // Setup authentication listener
        setupAuthListener()

        // Check authentication
        if (!isUserAuthenticated()) {
            redirectToLogin()
            return
        }

        // Initialize views and setup
        initializeViews()
        setupClickListeners()
        setTimeBasedGreeting()
        loadUserDataFromFirebase()
        showDailyMotivation()
    }

    private fun initializeFirebase() {
        authService = FirebaseAuthService()
        firestoreService = FirestoreService()
    }

    private fun setupAuthListener() {
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                // User signed out, redirect to login
                redirectToLogin()
            }
        }
    }

    private fun isUserAuthenticated(): Boolean {
        return authService.getCurrentUser() != null
    }

    private fun loadUserDataFromFirebase() {
        val userId = authService.getCurrentUser()?.uid ?: return

        lifecycleScope.launch {
            try {
                // Load user profile
                val profileResult = firestoreService.getUserProfile(userId)
                profileResult.onSuccess { profile ->
                    userProfile = profile
                    userName = profile.displayName.ifEmpty { "Champion" }
                    userNameText.text = "$userName!"
                }.onFailure { exception ->
                    Log.w("Dashboard", "Profile not found, using default: ${exception.message}")
                    // Create a basic profile if it doesn't exist
                    createUserProfileIfNeeded(userId)
                }

                // Load user stats
                val statsResult = firestoreService.getUserStats(userId)
                statsResult.onSuccess { stats ->
                    userStats = stats
                    updateStatsUI()
                }.onFailure { exception ->
                    showError("Failed to load stats: ${exception.message}")
                }

                // Load user workouts for today's workout
                val workoutsResult = firestoreService.getUserWorkouts(userId, 10)
                workoutsResult.onSuccess { workouts ->
                    setupTodaysWorkout(workouts)
                }.onFailure { exception ->
                    showError("Failed to load workouts: ${exception.message}")
                }
            } catch (e: Exception) {
                showError("Error loading data: ${e.message}")
            }
        }
    }

    private suspend fun createUserProfileIfNeeded(userId: String) {
        val user = authService.getCurrentUser()
        if (user != null) {
            val profile = UserProfile(
                uid = userId,
                displayName = user.displayName ?: "Champion",
                email = user.email ?: "",
                createdAt = System.currentTimeMillis()
            )

            firestoreService.createUserProfile(profile)
        }
    }

    private fun updateStatsUI() {
        workoutCountText.text = userStats.totalWorkouts.toString()
        caloriesText.text = userStats.totalCalories.toString()
        animateStatsUpdate()
    }

    private fun setupTodaysWorkout(workouts: List<WorkoutData>) {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        todayWorkout = when (today) {
            Calendar.MONDAY -> "Push Day - Chest & Triceps"
            Calendar.TUESDAY -> "Pull Day - Back & Biceps"
            Calendar.WEDNESDAY -> "Leg Day - Quads & Glutes"
            Calendar.THURSDAY -> "Push Day - Shoulders & Triceps"
            Calendar.FRIDAY -> "Pull Day - Back & Biceps"
            Calendar.SATURDAY -> "Full Body HIIT"
            Calendar.SUNDAY -> "Active Recovery - Yoga"
            else -> "Rest Day"
        }
        todayWorkoutDesc.text = todayWorkout
    }

    private fun loadUserName() {
        val user = authService.getCurrentUser()
        userName = user?.displayName ?: "Champion"
        userNameText.text = "$userName!"
    }

    private fun initializeViews() {
        // Header components
        greetingText = findViewById(R.id.greeting_text)
        userNameText = findViewById(R.id.user_name_text)
        profileImage = findViewById(R.id.profile_image)

        // Stats components
        workoutCountText = findViewById(R.id.workout_count_text)
        caloriesText = findViewById(R.id.calories_text)

        // Today's workout components
        todayWorkoutTitle = findViewById(R.id.today_workout_title)
        todayWorkoutDesc = findViewById(R.id.today_workout_desc)
        startWorkoutBtn = findViewById(R.id.start_workout_btn)

        // Quick action buttons
        progressBtn = findViewById(R.id.progress_btn)
        scheduleBtn = findViewById(R.id.schedule_btn)

        // Workout category cards
        strengthCard = findViewById(R.id.strength_card)
        cardioCard = findViewById(R.id.cardio_card)
        hiitCard = findViewById(R.id.hiit_card)
        yogaCard = findViewById(R.id.yoga_card)
        crossfitCard = findViewById(R.id.crossfit_card)
        pilatesCard = findViewById(R.id.pilates_card)
        bodyweightCard = findViewById(R.id.bodyweight_card)
        flexibilityCard = findViewById(R.id.flexibility_card)

        // Load user name after views are initialized
        loadUserName()
    }

    private fun setTimeBasedGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 0..11 -> "Good Morning,"
            in 12..16 -> "Good Afternoon,"
            else -> "Good Evening,"
        }
        greetingText.text = greeting
    }

    private fun setupClickListeners() {
        // Start workout button
        startWorkoutBtn.setOnClickListener { startTodayWorkout() }

        // Quick action buttons
        progressBtn.setOnClickListener { openProgressActivity() }
        scheduleBtn.setOnClickListener { openScheduleActivity() }
        profileImage.setOnClickListener { openProfileActivity() }

        // Workout category cards
        val workoutCategories = mapOf(
            strengthCard to "Strength Training",
            cardioCard to "Cardio",
            hiitCard to "HIIT",
            yogaCard to "Yoga",
            crossfitCard to "CrossFit",
            pilatesCard to "Pilates",
            bodyweightCard to "Bodyweight",
            flexibilityCard to "Flexibility"
        )

        workoutCategories.forEach { (card, category) ->
            card.setOnClickListener { openWorkoutCategory(category) }
        }
    }

    private fun openProgressActivity() {
        try {
            startActivity(Intent(this, ProgressActivity::class.java))
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top)
        } catch (e: Exception) {
            Toast.makeText(this, "Progress feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openScheduleActivity() {
        try {
            startActivity(Intent(this, ScheduleActivity::class.java))
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top)
        } catch (e: Exception) {
            Toast.makeText(this, "Schedule feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openProfileActivity() {
        try {
            startActivity(Intent(this, ProfileActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        } catch (e: Exception) {
            Toast.makeText(this, "Profile feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTodayWorkout() {
        try {
            val intent = Intent(this, WorkoutActivity::class.java).apply {
                putExtra("workout_type", "today")
                putExtra("workout_name", todayWorkout)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        } catch (e: Exception) {
            Toast.makeText(this, "Workout feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openWorkoutCategory(category: String) {
        try {
            val intent = Intent(this, WorkoutCategoryActivity::class.java).apply {
                putExtra("category", category)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            Toast.makeText(this, "Opening $category workouts", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "$category workouts coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun redirectToLogin() {
        try {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Login feature not implemented", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        authStateListener?.let { authService.addAuthStateListener(it) }
    }

    override fun onStop() {
        super.onStop()
        authStateListener?.let { authService.removeAuthStateListener(it) }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to the dashboard
        if (::userStats.isInitialized) {
            loadUserDataFromFirebase()
        }
    }

    private fun animateStatsUpdate() {
        listOf(workoutCountText, caloriesText).forEach { textView ->
            textView.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(200)
                .withEndAction {
                    textView.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(200)
                        .start()
                }
                .start()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        @Suppress("DEPRECATION")
        super.onBackPressed()
        showExitDialog()
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Exit") { _, _ -> finishAffinity() }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    fun updateWorkoutStats(completedWorkout: Boolean, caloriesBurned: Int, workoutData: WorkoutData? = null) {
        if (!completedWorkout) return

        val userId = authService.getCurrentUser()?.uid ?: return

        lifecycleScope.launch {
            try {
                // Save the workout data if provided
                workoutData?.let { workout ->
                    firestoreService.saveWorkout(userId, workout)
                }

                // Update user stats
                val newStats = userStats.copy(
                    totalWorkouts = userStats.totalWorkouts + 1,
                    totalCalories = userStats.totalCalories + caloriesBurned,
                    lastWorkoutDate = System.currentTimeMillis(),
                    currentStreak = calculateNewStreak()
                )

                val result = firestoreService.updateUserStats(userId, newStats)
                result.onSuccess {
                    userStats = newStats
                    updateStatsUI()
                    Toast.makeText(
                        this@Dashboard,
                        "Great job! Workout completed! 🔥",
                        Toast.LENGTH_LONG
                    ).show()
                }.onFailure { exception ->
                    showError("Failed to save progress: ${exception.message}")
                }
            } catch (e: Exception) {
                showError("Error updating stats: ${e.message}")
            }
        }
    }

    private fun calculateNewStreak(): Int {
        val lastWorkout = userStats.lastWorkoutDate
        val today = System.currentTimeMillis()
        val oneDayMs = 24 * 60 * 60 * 1000

        return if (today - lastWorkout <= oneDayMs) {
            userStats.currentStreak + 1
        } else {
            1 // Reset streak
        }
    }

    private fun getMotivationalQuote(): String {
        val quotes = arrayOf(
            "Push yourself, because no one else is going to do it for you.",
            "Great things never come from comfort zones.",
            "Don't stop when you're tired. Stop when you're done.",
            "Success starts with self-discipline.",
            "A champion is someone who gets up when they can't.",
            "Your only limit is your mind.",
            "Train like a beast, look like a beauty.",
            "Sweat is just fat crying."
        )
        return quotes.random()
    }

    private fun showDailyMotivation() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val sharedPref = getSharedPreferences("motivation", MODE_PRIVATE)
        val lastShown = sharedPref.getString("last_motivation_date", "")

        if (lastShown != today) {
            AlertDialog.Builder(this)
                .setTitle("Daily Motivation 💪")
                .setMessage(getMotivationalQuote())
                .setPositiveButton("Let's Go!") { dialog, _ -> dialog.dismiss() }
                .show()

            // Save today's date
            sharedPref.edit()
                .putString("last_motivation_date", today)
                .apply()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.e("Dashboard", message)
    }

    private fun logout() {
        authService.signOut()
        redirectToLogin()
    }

    // Placeholder activity classes - you'll need to implement these
    class ProgressActivity : AppCompatActivity()
    class ScheduleActivity : AppCompatActivity()
    class ProfileActivity : AppCompatActivity()
    class WorkoutActivity : AppCompatActivity()
    class WorkoutCategoryActivity : AppCompatActivity()
    class LoginActivity : AppCompatActivity()
}