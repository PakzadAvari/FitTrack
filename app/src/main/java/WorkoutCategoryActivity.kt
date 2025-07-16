package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WorkoutCategoryActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var categoryTitleText: TextView
    private lateinit var categoryDescText: TextView
    private lateinit var workoutsRecyclerView: RecyclerView

    private lateinit var workoutAdapter: CategoryWorkoutAdapter
    private val workouts = mutableListOf<CategoryWorkout>()
    private var category: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.workoutcategoryactivity)

        // Get category from intent
        category = intent.getStringExtra("category") ?: "General"

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        loadCategoryWorkouts()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.back_button)
        categoryTitleText = findViewById(R.id.category_title_text)
        categoryDescText = findViewById(R.id.category_desc_text)
        workoutsRecyclerView = findViewById(R.id.workouts_recycler_view)

        categoryTitleText.text = category
        categoryDescText.text = getCategoryDescription(category)
    }

    private fun setupRecyclerView() {
        workoutAdapter = CategoryWorkoutAdapter(workouts) { workout ->
            startWorkout(workout)
        }
        workoutsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        workoutsRecyclerView.adapter = workoutAdapter
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener { finish() }
    }

    private fun loadCategoryWorkouts() {
        workouts.clear()

        when (category.toLowerCase()) {
            "strength training" -> loadStrengthWorkouts()
            "cardio" -> loadCardioWorkouts()
            "hiit" -> loadHIITWorkouts()
            "yoga" -> loadYogaWorkouts()
            "crossfit" -> loadCrossFitWorkouts()
            "pilates" -> loadPilatesWorkouts()
            "bodyweight" -> loadBodyweightWorkouts()
            "flexibility" -> loadFlexibilityWorkouts()
            else -> loadGeneralWorkouts()
        }

        workoutAdapter.notifyDataSetChanged()
    }

    private fun loadStrengthWorkouts() {
        workouts.addAll(listOf(
            CategoryWorkout("Push Day", "Chest, Shoulders, Triceps", 45, 400, R.drawable.ic_strength),
            CategoryWorkout("Pull Day", "Back, Biceps, Rear Delts", 45, 380, R.drawable.ic_strength),
            CategoryWorkout("Leg Day", "Quads, Glutes, Hamstrings", 50, 450, R.drawable.ic_strength),
            CategoryWorkout("Upper Body", "Full Upper Body Workout", 40, 350, R.drawable.ic_strength),
            CategoryWorkout("Lower Body", "Full Lower Body Workout", 40, 400, R.drawable.ic_strength),
            CategoryWorkout("Full Body", "Complete Body Workout", 60, 500, R.drawable.ic_strength)
        ))
    }

    private fun loadCardioWorkouts() {
        workouts.addAll(listOf(
            CategoryWorkout("LISS Cardio", "Low Intensity Steady State", 30, 300, R.drawable.ic_cardio),
            CategoryWorkout("Sprint Training", "High Intensity Intervals", 20, 250, R.drawable.ic_cardio),
            CategoryWorkout("Endurance Run", "Long Distance Running", 45, 500, R.drawable.ic_cardio),
            CategoryWorkout("Cycling", "Indoor/Outdoor Cycling", 40, 400, R.drawable.ic_cardio),
            CategoryWorkout("Swimming", "Full Body Cardio", 30, 350, R.drawable.ic_cardio),
            CategoryWorkout("Dance Cardio", "Fun Dance Workout", 25, 280, R.drawable.ic_cardio)
        ))
    }

    private fun loadHIITWorkouts() {
        workouts.addAll(listOf(
            CategoryWorkout("Tabata", "4-minute High Intensity", 20, 200, R.drawable.ic_hiit),
            CategoryWorkout("EMOM", "Every Minute On Minute", 15, 180, R.drawable.ic_hiit),
            CategoryWorkout("Circuit Training", "Multiple Exercise Circuit", 30, 350, R.drawable.ic_hiit),
            CategoryWorkout("Bodyweight HIIT", "No Equipment Required", 25, 300, R.drawable.ic_hiit),
            CategoryWorkout("Cardio HIIT", "High Intensity Cardio", 20, 250, R.drawable.ic_hiit),
            CategoryWorkout("Strength HIIT", "Weights + Cardio", 35, 400, R.drawable.ic_hiit)
        ))
    }

    private fun loadYogaWorkouts() {
        workouts.addAll(listOf(
            CategoryWorkout("Vinyasa Flow", "Dynamic Yoga Flow", 45, 150, R.drawable.ic_yoga1),
            CategoryWorkout("Hatha Yoga", "Traditional Yoga Poses", 60, 120, R.drawable.ic_yoga1),
            CategoryWorkout("Power Yoga", "Strength-based Yoga", 50, 200, R.drawable.ic_yoga1),
            CategoryWorkout("Yin Yoga", "Restorative Yoga", 60, 100, R.drawable.ic_yoga1),
            CategoryWorkout("Morning Yoga", "Gentle Wake-up Flow", 20, 80, R.drawable.ic_yoga1),
            CategoryWorkout("Evening Yoga", "Relaxing Night Flow", 30, 100, R.drawable.ic_yoga1)
        ))
    }

    private fun loadCrossFitWorkouts() {
        workouts.addAll(listOf(
            CategoryWorkout("WOD - Fran", "Thrusters + Pull-ups", 15, 200, R.drawable.ic_crossfit),
            CategoryWorkout("WOD - Cindy", "AMRAP 20 minutes", 20, 250, R.drawable.ic_crossfit),
            CategoryWorkout("WOD - Murph", "Hero Workout", 45, 600, R.drawable.ic_crossfit),
            CategoryWorkout("Olympic Lifting", "Snatch + Clean & Jerk", 40, 300, R.drawable.ic_crossfit),
            CategoryWorkout("MetCon", "Metabolic Conditioning", 25, 350, R.drawable.ic_crossfit),
            CategoryWorkout("Strongman", "Functional Strength", 35, 400, R.drawable.ic_crossfit)
        ))
    }

    private fun loadPilatesWorkouts() {
        workouts.addAll(listOf(
            CategoryWorkout("Mat Pilates", "Floor-based Pilates", 45, 180, R.drawable.ic_pilates),
            CategoryWorkout("Reformer", "Equipment-based Pilates", 50, 200, R.drawable.ic_pilates),
            CategoryWorkout("Core Focus", "Abdominal Strengthening", 30, 150, R.drawable.ic_pilates),
            CategoryWorkout("Beginner Pilates", "Introduction to Pilates", 40, 160, R.drawable.ic_pilates),
            CategoryWorkout("Advanced Pilates", "Challenging Movements", 55, 220, R.drawable.ic_pilates),
            CategoryWorkout("Pilates Fusion", "Pilates + Yoga", 50, 200, R.drawable.ic_pilates)
        ))
    }

    private fun loadBodyweightWorkouts() {
        workouts.addAll(listOf(
            CategoryWorkout("Calisthenics", "Bodyweight Strength", 40, 300, R.drawable.ic_bodyweight),
            CategoryWorkout("Prison Workout", "Minimal Space Required", 35, 280, R.drawable.ic_bodyweight),
            CategoryWorkout("Playground", "Outdoor Bodyweight", 45, 350, R.drawable.ic_bodyweight),
            CategoryWorkout("Military Style", "Boot Camp Workout", 50, 400, R.drawable.ic_bodyweight),
            CategoryWorkout("Beginner Flow", "Easy Bodyweight", 30, 200, R.drawable.ic_bodyweight),
            CategoryWorkout("Advanced Flow", "Complex Movements", 45, 380, R.drawable.ic_bodyweight)
        ))
    }

    private fun loadFlexibilityWorkouts() {
        workouts.addAll(listOf(
            CategoryWorkout("Full Body Stretch", "Complete Flexibility", 30, 80, R.drawable.ic_flexibility),
            CategoryWorkout("Hip Mobility", "Hip Flexor Focus", 20, 60, R.drawable.ic_flexibility),
            CategoryWorkout("Shoulder Mobility", "Upper Body Flexibility", 25, 70, R.drawable.ic_flexibility),
            CategoryWorkout("Post-Workout", "Recovery Stretching", 15, 50, R.drawable.ic_flexibility),
            CategoryWorkout("Morning Stretch", "Wake-up Routine", 10, 40, R.drawable.ic_flexibility),
            CategoryWorkout("Desk Stretches", "Office-friendly", 12, 45, R.drawable.ic_flexibility)
        ))
    }

    private fun loadGeneralWorkouts() {
        workouts.addAll(listOf(
            CategoryWorkout("Quick Burn", "15-minute Workout", 15, 150, R.drawable.ic_general),
            CategoryWorkout("Full Body", "Complete Workout", 45, 400, R.drawable.ic_general),
            CategoryWorkout("Beginner", "Easy Start", 30, 200, R.drawable.ic_general),
            CategoryWorkout("Intermediate", "Medium Difficulty", 40, 350, R.drawable.ic_general),
            CategoryWorkout("Advanced", "High Intensity", 50, 500, R.drawable.ic_general),
            CategoryWorkout("Recovery", "Active Recovery", 25, 150, R.drawable.ic_general)
        ))
    }

    private fun getCategoryDescription(category: String): String {
        return when (category.toLowerCase()) {
            "strength training" -> "Build muscle and increase strength with progressive overload"
            "cardio" -> "Improve cardiovascular health and endurance"
            "hiit" -> "High-intensity intervals for maximum calorie burn"
            "yoga" -> "Improve flexibility, balance, and mindfulness"
            "crossfit" -> "Functional fitness with varied movements"
            "pilates" -> "Core strength and body alignment"
            "bodyweight" -> "No equipment needed, use your body weight"
            "flexibility" -> "Improve mobility and reduce muscle tension"
            else -> "Various workout types for all fitness levels"
        }
    }

    private fun startWorkout(workout: CategoryWorkout) {
        val intent = Intent(this, WorkoutActivity::class.java).apply {
            putExtra("workout_type", category.toLowerCase().replace(" ", "_"))
            putExtra("workout_name", workout.name)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}

data class CategoryWorkout(
    val name: String,
    val description: String,
    val duration: Int,
    val calories: Int,
    val imageResource: Int
)

class CategoryWorkoutAdapter(
    private val workouts: List<CategoryWorkout>,
    private val onWorkoutClick: (CategoryWorkout) -> Unit
) : RecyclerView.Adapter<CategoryWorkoutAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val workoutImage: ImageView = itemView.findViewById(R.id.workout_image)
        val workoutName: TextView = itemView.findViewById(R.id.workout_name)
        val workoutDescription: TextView = itemView.findViewById(R.id.workout_description)
        val workoutDuration: TextView = itemView.findViewById(R.id.workout_duration)
        val workoutCalories: TextView = itemView.findViewById(R.id.workout_calories)
        val workoutCard: androidx.cardview.widget.CardView = itemView.findViewById(R.id.workout_card)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]

        holder.workoutName.text = workout.name
        holder.workoutDescription.text = workout.description
        holder.workoutDuration.text = "${workout.duration} min"
        holder.workoutCalories.text = "${workout.calories} cal"
        holder.workoutImage.setImageResource(workout.imageResource)

        holder.workoutCard.setOnClickListener {
            onWorkoutClick(workout)
        }

        // Add some animation on click
        holder.workoutCard.setOnTouchListener { view, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    view.animate()
                        .scaleX(0.95f)
                        .scaleY(0.95f)
                        .setDuration(100)
                        .start()
                }
                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                    view.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(100)
                        .start()
                }
            }
            false
        }
    }

    override fun getItemCount(): Int = workouts.size
}