package com.example.fitnessapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ScheduleActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var scheduleRecyclerView: RecyclerView
    private lateinit var addWorkoutButton: Button

    private lateinit var scheduleAdapter: ScheduleAdapter
    private val scheduleItems = mutableListOf<ScheduleItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        loadScheduleData()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.back_button)
        scheduleRecyclerView = findViewById(R.id.schedule_recycler_view)
        addWorkoutButton = findViewById(R.id.add_workout_button)
    }

    private fun setupRecyclerView() {
        scheduleAdapter = ScheduleAdapter(scheduleItems) { scheduleItem ->
            // Handle schedule item click
            Toast.makeText(this, "Starting ${scheduleItem.workoutName}", Toast.LENGTH_SHORT).show()
        }
        scheduleRecyclerView.layoutManager = LinearLayoutManager(this)
        scheduleRecyclerView.adapter = scheduleAdapter
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener { finish() }
        addWorkoutButton.setOnClickListener { addNewWorkout() }
    }

    private fun loadScheduleData() {
        // Generate sample schedule data
        scheduleItems.clear()

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())

        // Add workouts for the next 7 days
        for (i in 0 until 7) {
            val date = dateFormat.format(calendar.time)
            val workoutName = getWorkoutForDay(calendar.get(Calendar.DAY_OF_WEEK))
            val time = if (i == 0) "Now" else "7:00 AM"
            val isCompleted = i < 0 // No completed workouts in future

            scheduleItems.add(ScheduleItem(
                date = date,
                workoutName = workoutName,
                time = time,
                isCompleted = isCompleted,
                workoutType = getWorkoutType(workoutName)
            ))

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        scheduleAdapter.notifyDataSetChanged()
    }

    private fun getWorkoutForDay(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.MONDAY -> "Push Day - Chest & Triceps"
            Calendar.TUESDAY -> "Pull Day - Back & Biceps"
            Calendar.WEDNESDAY -> "Leg Day - Quads & Glutes"
            Calendar.THURSDAY -> "Push Day - Shoulders & Triceps"
            Calendar.FRIDAY -> "Pull Day - Back & Biceps"
            Calendar.SATURDAY -> "Full Body HIIT"
            Calendar.SUNDAY -> "Active Recovery - Yoga"
            else -> "Rest Day"
        }
    }

    private fun getWorkoutType(workoutName: String): String {
        return when {
            workoutName.contains("Push") -> "strength"
            workoutName.contains("Pull") -> "strength"
            workoutName.contains("Leg") -> "strength"
            workoutName.contains("HIIT") -> "hiit"
            workoutName.contains("Yoga") -> "yoga"
            else -> "general"
        }
    }

    private fun addNewWorkout() {
        Toast.makeText(this, "Add workout feature coming soon!", Toast.LENGTH_SHORT).show()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom)
    }
}

data class ScheduleItem(
    val date: String,
    val workoutName: String,
    val time: String,
    val isCompleted: Boolean,
    val workoutType: String
)

class ScheduleAdapter(
    private val scheduleItems: List<ScheduleItem>,
    private val onItemClick: (ScheduleItem) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    class ScheduleViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.date_text)
        val workoutNameText: TextView = itemView.findViewById(R.id.workout_name_text)
        val timeText: TextView = itemView.findViewById(R.id.time_text)
        val statusIcon: ImageView = itemView.findViewById(R.id.status_icon)
        val cardView: androidx.cardview.widget.CardView = itemView.findViewById(R.id.schedule_card)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = android.view.LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val item = scheduleItems[position]

        holder.dateText.text = item.date
        holder.workoutNameText.text = item.workoutName
        holder.timeText.text = item.time

        if (item.isCompleted) {
            holder.statusIcon.setImageResource(R.drawable.ic_check_circle)
            holder.cardView.alpha = 0.7f
        } else {
            holder.statusIcon.setImageResource(R.drawable.ic_play_circle)
            holder.cardView.alpha = 1.0f
        }

        holder.cardView.setOnClickListener {
            if (!item.isCompleted) {
                onItemClick(item)
            }
        }
    }

    override fun getItemCount(): Int = scheduleItems.size
}