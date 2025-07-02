package com.example.fitnessapp.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await
import kotlin.Result

data class WorkoutData(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val duration: Int = 0,
    val caloriesBurned: Int = 0,
    val date: Long = System.currentTimeMillis(),
    val completed: Boolean = false
)

data class UserStats(
    val totalWorkouts: Int = 0,
    val totalCalories: Int = 0,
    val currentStreak: Int = 0,
    val lastWorkoutDate: Long = 0
)

class FirestoreService {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // User Management
    suspend fun createUserProfile(userId: String, userName: String): Result<Unit> {
        return try {
            val userProfile: Map<String, Any> = mapOf(
                "name" to userName,
                "joinDate" to System.currentTimeMillis(),
                "stats" to mapOf(
                    "totalWorkouts" to 0,
                    "totalCalories" to 0,
                    "currentStreak" to 0,
                    "lastWorkoutDate" to 0L
                )
            )
            db.collection("users").document(userId).set(userProfile).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Workout Operations
    suspend fun saveWorkout(userId: String, workout: WorkoutData): Result<String> {
        return try {
            val workoutMap: Map<String, Any> = mapOf(
                "name" to workout.name,
                "category" to workout.category,
                "duration" to workout.duration,
                "caloriesBurned" to workout.caloriesBurned,
                "date" to workout.date,
                "completed" to workout.completed
            )

            val docRef = db.collection("users")
                .document(userId)
                .collection("workouts")
                .add(workoutMap)
                .await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserWorkouts(userId: String): Result<List<WorkoutData>> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("workouts")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val workouts: List<WorkoutData> = snapshot.documents.mapNotNull { doc: DocumentSnapshot ->
                try {
                    WorkoutData(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        category = doc.getString("category") ?: "",
                        duration = doc.getLong("duration")?.toInt() ?: 0,
                        caloriesBurned = doc.getLong("caloriesBurned")?.toInt() ?: 0,
                        date = doc.getLong("date") ?: System.currentTimeMillis(),
                        completed = doc.getBoolean("completed") ?: false
                    )
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(workouts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserStats(userId: String, stats: UserStats): Result<Unit> {
        return try {
            val statsMap: Map<String, Any> = mapOf(
                "totalWorkouts" to stats.totalWorkouts,
                "totalCalories" to stats.totalCalories,
                "currentStreak" to stats.currentStreak,
                "lastWorkoutDate" to stats.lastWorkoutDate
            )
            db.collection("users")
                .document(userId)
                .update("stats", statsMap)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserStats(userId: String): Result<UserStats> {
        return try {
            val snapshot: DocumentSnapshot = db.collection("users")
                .document(userId)
                .get()
                .await()

            val stats = snapshot.get("stats") as? Map<*, *>
            val userStats = UserStats(
                totalWorkouts = (stats?.get("totalWorkouts") as? Number)?.toInt() ?: 0,
                totalCalories = (stats?.get("totalCalories") as? Number)?.toInt() ?: 0,
                currentStreak = (stats?.get("currentStreak") as? Number)?.toInt() ?: 0,
                lastWorkoutDate = (stats?.get("lastWorkoutDate") as? Number)?.toLong() ?: 0L
            )
            Result.success(userStats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}