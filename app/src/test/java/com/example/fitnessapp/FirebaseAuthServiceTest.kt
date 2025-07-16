package com.example.fitnessapp

import com.example.fitnessapp.com.example.fitnessapp.FirebaseAuthService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class FirebaseAuthServiceTest {

    private val mockAuth = mockk<FirebaseAuth>()
    private val mockUser = mockk<FirebaseUser>()
    private val mockAuthResult = mockk<AuthResult>()

    private lateinit var authService: FirebaseAuthService

    @Before
    fun setUp() {
        // Mock FirebaseAuth.getInstance()
        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns mockAuth

        authService = FirebaseAuthService()
    }

    @After
    fun tearDown() {
        // Clean up all mocks
        unmockkAll()
    }

    @Test
    fun `signUp success should return user`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"

        every { mockAuthResult.user } returns mockUser
        every { mockAuth.createUserWithEmailAndPassword(email, password) } returns Tasks.forResult(mockAuthResult)

        // When
        val result = authService.signUp(email, password)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockUser, result.getOrNull())
        verify { mockAuth.createUserWithEmailAndPassword(email, password) }
    }

    @Test
    fun `signUp failure should return error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val exception = Exception("Authentication failed")

        every { mockAuth.createUserWithEmailAndPassword(email, password) } returns Tasks.forException(exception)

        // When
        val result = authService.signUp(email, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `signIn success should return user`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"

        every { mockAuthResult.user } returns mockUser
        every { mockAuth.signInWithEmailAndPassword(email, password) } returns Tasks.forResult(mockAuthResult)

        // When
        val result = authService.signIn(email, password)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockUser, result.getOrNull())
    }

    @Test
    fun `signIn failure should return error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val exception = Exception("Authentication failed")

        every { mockAuth.signInWithEmailAndPassword(email, password) } returns Tasks.forException(exception)

        // When
        val result = authService.signIn(email, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `getCurrentUser should return current user`() {
        // Given
        every { mockAuth.currentUser } returns mockUser

        // When
        val result = authService.getCurrentUser()

        // Then
        assertEquals(mockUser, result)
    }

    @Test
    fun `getCurrentUser should return null when no user logged in`() {
        // Given
        every { mockAuth.currentUser } returns null

        // When
        val result = authService.getCurrentUser()

        // Then
        assertEquals(null, result)
    }

    @Test
    fun `signOut should call auth signOut`() {
        // Given
        every { mockAuth.signOut() } just Runs

        // When
        authService.signOut()

        // Then
        verify { mockAuth.signOut() }
    }
}