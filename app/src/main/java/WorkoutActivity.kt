package com.example.fitnessapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.com.example.fitnessapp.FirebaseAuthService
import kotlinx.coroutines.launch

class WorkoutActivity : AppCompatActivity() {

    private lateinit var authService: FirebaseAuthService
    private lateinit var firestoreService: FirestoreService

    private lateinit var backButton: ImageView
    private lateinit var workoutTitleText: TextView
    private lateinit var timerText: TextView
    private lateinit var startPauseButton: Button
    private lateinit var finishButton: Button
    private lateinit var exerciseRecyclerView: RecyclerView
    private lateinit var caloriesText: TextView

    private lateinit var exerciseAdapter: ExerciseAdapter
    private val exercises = mutableListOf<Exercise>()
    private var workoutTimer: CountDownTimer? = null
    private var isTimerRunning = false
    private var elapsedTime = 0L
    private var startTime = 0L
    private var estimatedCalories = 0

    private var workoutType: String = ""
    private var workoutName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        // Initialize Firebase services
        authService = FirebaseAuthService()
        firestoreService = FirestoreService()

        // Get workout details from intent
        workoutType = intent.getStringExtra("workout_type") ?: "general"
        workoutName = intent.getStringExtra("workout_name") ?: "Workout"

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        loadWorkoutExercises()
        setupWorkoutTimer()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.back_button)
        workoutTitleText = findViewById(R.id.workout_title_text)
        timerText = findViewById(R.id.timer_text)
        startPauseButton = findViewById(R.id.start_pause_button)
        finishButton = findViewById(R.id.finish_button)
        exerciseRecyclerView = findViewById(R.id.exercise_recycler_view)
        caloriesText = findViewById(R.id.calories_text)

        workoutTitleText.text = workoutName
        timerText.text = "00:00"
        caloriesText.text = "0 kcal"
    }

    private fun setupRecyclerView() {
        exerciseAdapter = ExerciseAdapter(exercises) { exercise ->
            // Handle exercise completion
            updateCalories()
        }
        exerciseRecyclerView.layoutManager = LinearLayoutManager(this)
        exerciseRecyclerView.adapter = exerciseAdapter
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener { showExitDialog() }
        startPauseButton.setOnClickListener { toggleTimer() }
        finishButton.setOnClickListener { finishWorkout() }
    }

    private fun loadWorkoutExercises() {
        // Load exercises based on workout type
        when (workoutType) {
            "today" -> loadTodayWorkoutExercises()
            "strength" -> loadStrengthExercises()
            "cardio" -> loadCardioExercises()
            "hiit" -> loadHIITExercises()
            "yoga" -> loadYogaExercises()
            else -> loadDefaultExercises()
        }

        exerciseAdapter.notifyDataSetChanged()
    }

    private fun loadTodayWorkoutExercises() {
        exercises.clear()
        exercises.addAll(listOf(
            Exercise("Push-ups", 3, 12, 0.0, 0),
            Exercise("Bench Press", 3, 10, 60.0, 0),
            Exercise("Tricep Dips", 3, 15, 0.0, 0),
            Exercise("Overhead Press", 3, 8, 40.0, 0),
            Exercise("Tricep Extensions", 3, 12, 25.0, 0),
            Exercise("Chest Flyes", 3, 10, 30.0, 0)
        ))
        estimatedCalories = 350
    }

    private fun loadStrengthExercises() {
        exercises.clear()
        exercises.addAll(listOf(
            Exercise("Squats", 4, 12, 80.0, 0),
            Exercise("Deadlifts", 4, 8, 100.0, 0),
            Exercise("Bench Press", 4, 10, 70.0, 0),
            Exercise("Pull-ups", 3, 8, 0.0, 0),
            Exercise("Overhead Press", 3, 10, 50.0, 0)
        ))
        estimatedCalories = 400
    }

    private fun loadCardioExercises() {
        exercises.clear()
        exercises.addAll(listOf(
            Exercise("Running", 0, 0, 0.0, 30),
            Exercise("Cycling", 0, 0, 0.0, 25),
            Exercise("Jump Rope", 0, 0, 0.0, 10),
            Exercise("Burpees", 3, 10, 0.0, 0),
            Exercise("Mountain Climbers", 3, 20, 0.0, 0)
        ))
        estimatedCalories = 450
    }

    private fun loadHIITExercises() {
        exercises.clear()
        exercises.addAll(listOf(
            Exercise("Burpees", 4, 10, 0.0, 0),
            Exercise("Jump Squats", 4, 15, 0.0, 0),
            Exercise("Push-ups", 4, 12, 0.0, 0),
            Exercise("Mountain Climbers", 4, 20, 0.0, 0),
            Exercise("Plank", 4, 0, 0.0, 45),
            Exercise("High Knees", 4, 30, 0.0, 0)
        ))
        estimatedCalories = 500
    }

    private fun loadYogaExercises() {
        exercises.clear()
        exercises.addAll(listOf(
            Exercise("Downward Dog", 0, 0, 0.0, 60),
            Exercise("Warrior I", 0, 0, 0.0, 30),
            Exercise("Warrior II", 0, 0, 0.0, 30),
            Exercise("Tree Pose", 0, 0, 0.0, 45),
            Exercise("Child's Pose", 0, 0, 0.0, 60),
            Exercise("Cobra Pose", 0, 0, 0.0, 30)
        ))
        estimatedCalories = 200
    }

    private fun loadDefaultExercises() {
        exercises.clear()
        exercises.addAll(listOf(
            Exercise("Jumping Jacks", 3, 20, 0.0, 0),
            Exercise("Push-ups", 3, 10, 0.0, 0),
            Exercise("Squats", 3, 15, 0.0, 0),
            Exercise("Plank", 3, 0, 0.0, 30)
        ))
        estimatedCalories = 250
    }

    private fun setupWorkoutTimer() {
        startTime = System.currentTimeMillis()
    }

    private fun toggleTimer() {
        if (isTimerRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        isTimerRunning = true
        startPauseButton.text = "Pause"

        workoutTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                elapsedTime = System.currentTimeMillis() - startTime
                updateTimerDisplay()
                updateCalories()
            }

            override fun onFinish() {
                // Timer finished
            }
        }.start()
    }

    private fun pauseTimer() {
        isTimerRunning = false
        startPauseButton.text = "Resume"
        workoutTimer?.cancel()
    }

    private fun updateTimerDisplay() {
        val minutes = (elapsedTime / 1000) / 60
        val seconds = (elapsedTime / 1000) % 60
        timerText.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateCalories() {
        val minutesElapsed = (elapsedTime / 1000) / 60
        val caloriesPerMinute = estimatedCalories / 45.0 // Assuming 45 min workout
        val currentCalories = (minutesElapsed * caloriesPerMinute).toInt()
        caloriesText.text = "$currentCalories kcal"
    }

    private fun finishWorkout() {
        pauseTimer()

        AlertDialog.Builder(this)
            .setTitle("Finish Workout")
            .setMessage("Great job! Are you ready to finish this workout?")
            .setPositiveButton("Finish") { _, _ ->
                completeWorkout()
            }
            .setNegativeButton("Continue") { dialog, _ ->
                dialog.dismiss()
                if (isTimerRunning) startTimer()
            }
            .show()
    }

    private fun completeWorkout() {
        val finalDuration = (elapsedTime / 1000 / 60).toInt() // in minutes
        val finalCalories = caloriesText.text.toString().replace(" kcal", "").toIntOrNull() ?: 0

        val workoutData = WorkoutData(
            name = workoutName,
            type = workoutType,
            duration = finalDuration,
            calories = finalCalories,
            date = System.currentTimeMillis(),
            exercises = exercises
        )

        // Save workout to Firebase
        val userId = authService.getCurrentUser()?.uid
        if (userId != null) {
            lifecycleScope.launch {
                try {
                    val result = firestoreService.saveWorkout(userId, workoutData)
                    result.onSuccess {
                        Toast.makeText(this@WorkoutActivity, "Workout saved successfully!", Toast.LENGTH_SHORT).show()

                        // Return workout data to the calling activity (Dashboard)
                        val resultIntent = Intent().apply {
                            putExtra("workout_completed", true)
                            putExtra("calories_burned", finalCalories)
                            putExtra("workout_duration", finalDuration)
                            putExtra("workout_name", workoutName)
                            putExtra("workout_type", workoutType)
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }.onFailure { exception ->
                        Toast.makeText(this@WorkoutActivity, "Failed to save workout: ${exception.message}", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@WorkoutActivity, "Error saving workout: ${e.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } else {
            finish()
        }
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exit Workout")
            .setMessage("Are you sure you want to exit? Your progress will be lost.")
            .setPositiveButton("Exit") { _, _ -> finish() }
            .setNegativeButton("Continue") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        workoutTimer?.cancel()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}

class ExerciseAdapter(
    private val exercises: List<Exercise>,
    private val onExerciseCompleted: (Exercise) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.exercise_name)
        val exerciseDetails: TextView = itemView.findViewById(R.id.exercise_details)
        val completeButton: Button = itemView.findViewById(R.id.complete_button)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = android.view.LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.exerciseName.text = exercise.name

        val details = when {
            exercise.sets > 0 && exercise.reps > 0 -> "${exercise.sets} sets × ${exercise.reps} reps"
            exercise.sets > 0 && exercise.duration > 0 -> "${exercise.sets} sets × ${exercise.duration}s"
            exercise.duration > 0 -> "${exercise.duration} seconds"
            else -> "Complete exercise"
        }

        if (exercise.weight > 0) {
            holder.exerciseDetails.text = "$details @ ${exercise.weight}kg"
        } else {
            holder.exerciseDetails.text = details
        }

        holder.completeButton.setOnClickListener {
            it.isEnabled = false
            (it as Button).text = "✓ Done"
            onExerciseCompleted(exercise)
        }
    }

    override fun getItemCount(): Int = exercises.size
}