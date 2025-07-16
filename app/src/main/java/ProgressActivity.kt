package com.example.fitnessapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.com.example.fitnessapp.FirebaseAuthService
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ProgressActivity : AppCompatActivity() {

    private lateinit var authService: FirebaseAuthService
    private lateinit var firestoreService: FirestoreService

    private lateinit var backButton: ImageView
    private lateinit var totalWorkoutsText: TextView
    private lateinit var totalCaloriesText: TextView
    private lateinit var currentStreakText: TextView
    private lateinit var thisWeekWorkoutsText: TextView
    private lateinit var thisWeekCaloriesText: TextView
    private lateinit var workoutHistoryRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    private lateinit var workoutAdapter: WorkoutHistoryAdapter
    private val workoutHistory = mutableListOf<WorkoutData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        // Initialize Firebase services
        authService = FirebaseAuthService()
        firestoreService = FirestoreService()

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        loadProgressData()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.back_button)
        totalWorkoutsText = findViewById(R.id.total_workouts_text)
        totalCaloriesText = findViewById(R.id.total_calories_text)
        currentStreakText = findViewById(R.id.current_streak_text)
        thisWeekWorkoutsText = findViewById(R.id.this_week_workouts_text)
        thisWeekCaloriesText = findViewById(R.id.this_week_calories_text)
        workoutHistoryRecyclerView = findViewById(R.id.workout_history_recycler_view)
        progressBar = findViewById(R.id.progress_bar)
    }

    private fun setupRecyclerView() {
        workoutAdapter = WorkoutHistoryAdapter(workoutHistory)
        workoutHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        workoutHistoryRecyclerView.adapter = workoutAdapter
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener { finish() }
    }

    private fun loadProgressData() {
        val userId = authService.getCurrentUser()?.uid ?: return

        progressBar.visibility = android.view.View.VISIBLE

        lifecycleScope.launch {
            try {
                // Load user stats
                val statsResult = firestoreService.getUserStats(userId)
                statsResult.onSuccess { stats ->
                    updateStatsUI(stats)
                }.onFailure { exception ->
                    showError("Failed to load stats: ${exception.message}")
                }

                // Load weekly stats
                val weeklyStatsResult = firestoreService.getWeeklyStats(userId)
                weeklyStatsResult.onSuccess { weeklyStats ->
                    updateWeeklyStatsUI(weeklyStats)
                }.onFailure { exception ->
                    showError("Failed to load weekly stats: ${exception.message}")
                }

                // Load workout history
                val workoutsResult = firestoreService.getUserWorkouts(userId, 20)
                workoutsResult.onSuccess { workouts ->
                    workoutHistory.clear()
                    workoutHistory.addAll(workouts)
                    workoutAdapter.notifyDataSetChanged()
                }.onFailure { exception ->
                    showError("Failed to load workout history: ${exception.message}")
                }

                progressBar.visibility = android.view.View.GONE
            } catch (e: Exception) {
                progressBar.visibility = android.view.View.GONE
                showError("Error loading progress data: ${e.message}")
            }
        }
    }

    private fun updateStatsUI(stats: UserStats) {
        totalWorkoutsText.text = stats.totalWorkouts.toString()
        totalCaloriesText.text = "${stats.totalCalories} kcal"
        currentStreakText.text = "${stats.currentStreak} days"
    }

    private fun updateWeeklyStatsUI(weeklyStats: Map<String, Int>) {
        thisWeekWorkoutsText.text = weeklyStats["totalWorkouts"]?.toString() ?: "0"
        thisWeekCaloriesText.text = "${weeklyStats["totalCalories"] ?: 0} kcal"
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom)
    }
}

class WorkoutHistoryAdapter(private val workouts: List<WorkoutData>) : RecyclerView.Adapter<WorkoutHistoryAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val workoutName: TextView = itemView.findViewById(R.id.workout_name)
        val workoutType: TextView = itemView.findViewById(R.id.workout_type)
        val workoutDate: TextView = itemView.findViewById(R.id.workout_date)
        val workoutDuration: TextView = itemView.findViewById(R.id.workout_duration)
        val workoutCalories: TextView = itemView.findViewById(R.id.workout_calories)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = android.view.LayoutInflater.from(parent.context).inflate(R.layout.item_workout_history, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        holder.workoutName.text = workout.name
        holder.workoutType.text = workout.type
        holder.workoutDuration.text = "${workout.duration} min"
        holder.workoutCalories.text = "${workout.calories} kcal"

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        holder.workoutDate.text = dateFormat.format(Date(workout.date))
    }

    override fun getItemCount(): Int = workouts.size
}