package com.example.fitnessapp.services

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class FirestoreServiceTest {

    @Mock
    private lateinit var mockFirestore: FirebaseFirestore

    @Mock
    private lateinit var mockCollection: CollectionReference

    @Mock
    private lateinit var mockDocument: DocumentReference

    @Mock
    private lateinit var mockDocumentSnapshot: DocumentSnapshot

    @Mock
    private lateinit var mockQuerySnapshot: QuerySnapshot

    @Mock
    private lateinit var mockQuery: Query

    private lateinit var firestoreService: FirestoreService
    private lateinit var mockedStatic: MockedStatic<FirebaseFirestore>

    @Before
    fun setUp() {
        mockedStatic = mockStatic(FirebaseFirestore::class.java)
        mockedStatic.`when`<FirebaseFirestore> { FirebaseFirestore.getInstance() }.thenReturn(mockFirestore)

        firestoreService = FirestoreService()
    }

    @After
    fun tearDown() {
        mockedStatic.close()
    }

    @Test
    fun `createUserProfile success should return success result`() = runTest {
        // Given
        val userId = "user123"
        val userName = "John Doe"
        val email = "john@example.com"
        val phone = "1234567890"

        `when`(mockFirestore.collection("users")).thenReturn(mockCollection)
        `when`(mockCollection.document(userId)).thenReturn(mockDocument)
        `when`(mockDocument.set(any())).thenReturn(Tasks.forResult(null))

        // When
        val result = firestoreService.createUserProfile(userId, userName, email, phone)

        // Then
        assertTrue(result.isSuccess)
        verify(mockDocument).set(any())
    }

    @Test
    fun `createUserProfile failure should return error result`() = runTest {
        // Given
        val userId = "user123"
        val userName = "John Doe"
        val email = "john@example.com"
        val phone = "1234567890"
        val exception = Exception("Firestore error")

        `when`(mockFirestore.collection("users")).thenReturn(mockCollection)
        `when`(mockCollection.document(userId)).thenReturn(mockDocument)
        `when`(mockDocument.set(any())).thenReturn(Tasks.forException(exception))

        // When
        val result = firestoreService.createUserProfile(userId, userName, email, phone)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `saveWorkout success should return document ID`() = runTest {
        // Given
        val userId = "user123"
        val workout = WorkoutData(
            name = "Running",
            category = "Cardio",
            duration = 30,
            caloriesBurned = 200,
            completed = true
        )
        val expectedDocId = "workout123"

        `when`(mockFirestore.collection("users")).thenReturn(mockCollection)
        `when`(mockCollection.document(userId)).thenReturn(mockDocument)
        `when`(mockDocument.collection("workouts")).thenReturn(mockCollection)
        `when`(mockCollection.add(any())).thenReturn(Tasks.forResult(mockDocument))
        `when`(mockDocument.id).thenReturn(expectedDocId)

        // When
        val result = firestoreService.saveWorkout(userId, workout)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedDocId, result.getOrNull())
    }

    @Test
    fun `getUserWorkouts success should return workout list`() = runTest {
        // Given
        val userId = "user123"
        val workoutDocs = listOf(mockDocumentSnapshot)

        `when`(mockFirestore.collection("users")).thenReturn(mockCollection)
        `when`(mockCollection.document(userId)).thenReturn(mockDocument)
        `when`(mockDocument.collection("workouts")).thenReturn(mockCollection)
        `when`(mockCollection.orderBy("date", Query.Direction.DESCENDING)).thenReturn(mockQuery)
        `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
        `when`(mockQuerySnapshot.documents).thenReturn(workoutDocs)

        // Mock document data
        `when`(mockDocumentSnapshot.id).thenReturn("workout123")
        `when`(mockDocumentSnapshot.getString("name")).thenReturn("Running")
        `when`(mockDocumentSnapshot.getString("category")).thenReturn("Cardio")
        `when`(mockDocumentSnapshot.getLong("duration")).thenReturn(30L)
        `when`(mockDocumentSnapshot.getLong("caloriesBurned")).thenReturn(200L)
        `when`(mockDocumentSnapshot.getLong("date")).thenReturn(System.currentTimeMillis())
        `when`(mockDocumentSnapshot.getBoolean("completed")).thenReturn(true)

        // When
        val result = firestoreService.getUserWorkouts(userId)

        // Then
        assertTrue(result.isSuccess)
        val workouts = result.getOrNull()
        assertEquals(1, workouts?.size)
        assertEquals("Running", workouts?.first()?.name)
    }

    @Test
    fun `updateUserStats success should return success result`() = runTest {
        // Given
        val userId = "user123"
        val stats = UserStats(
            totalWorkouts = 10,
            totalCalories = 1000,
            currentStreak = 5,
            lastWorkoutDate = System.currentTimeMillis()
        )

        `when`(mockFirestore.collection("users")).thenReturn(mockCollection)
        `when`(mockCollection.document(userId)).thenReturn(mockDocument)
        `when`(mockDocument.update(eq("stats"), any())).thenReturn(Tasks.forResult(null))

        // When
        val result = firestoreService.updateUserStats(userId, stats)

        // Then
        assertTrue(result.isSuccess)
        verify(mockDocument).update(eq("stats"), any())
    }

    @Test
    fun `getUserStats success should return user stats`() = runTest {
        // Given
        val userId = "user123"
        val statsMap = mapOf(
            "totalWorkouts" to 10,
            "totalCalories" to 1000,
            "currentStreak" to 5,
            "lastWorkoutDate" to System.currentTimeMillis()
        )

        `when`(mockFirestore.collection("users")).thenReturn(mockCollection)
        `when`(mockCollection.document(userId)).thenReturn(mockDocument)
        `when`(mockDocument.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
        `when`(mockDocumentSnapshot.get("stats")).thenReturn(statsMap)

        // When
        val result = firestoreService.getUserStats(userId)

        // Then
        assertTrue(result.isSuccess)
        val stats = result.getOrNull()
        assertEquals(10, stats?.totalWorkouts)
        assertEquals(1000, stats?.totalCalories)
        assertEquals(5, stats?.currentStreak)
    }
}