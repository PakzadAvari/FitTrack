package com.example.fitnessapp

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(login::class.java)

    @Test
    fun testLoginFormValidation() {
        // Test empty email
        onView(withId(R.id.loginButton)).perform(click())
        onView(withId(R.id.emailInputLayout)).check(matches(hasDescendant(withText("Email is required"))))

        // Test invalid email
        onView(withId(R.id.emailEditText)).perform(typeText("invalid.email"))
        onView(withId(R.id.loginButton)).perform(click())
        onView(withId(R.id.emailInputLayout)).check(matches(hasDescendant(withText("Please enter a valid email address"))))

        // Test empty password
        onView(withId(R.id.emailEditText)).perform(clearText(), typeText("test@example.com"))
        onView(withId(R.id.loginButton)).perform(click())
        onView(withId(R.id.passwordInputLayout)).check(matches(hasDescendant(withText("Password is required"))))

        // Test short password
        onView(withId(R.id.passwordEditText)).perform(typeText("123"))
        onView(withId(R.id.loginButton)).perform(click())
        onView(withId(R.id.passwordInputLayout)).check(matches(hasDescendant(withText("Password must be at least 6 characters"))))
    }

    @Test
    fun testNavigationToSignup() {
        // Click on signup text
        onView(withId(R.id.signupText)).perform(click())

        // Verify signup activity is launched
        // Add proper intent verification here
    }

    @Test
    fun testErrorClearingOnFocus() {
        // Set an error first
        onView(withId(R.id.loginButton)).perform(click())
        onView(withId(R.id.emailInputLayout)).check(matches(hasDescendant(withText("Email is required"))))

        // Focus on email field - error should clear
        onView(withId(R.id.emailEditText)).perform(click())
        // You might need to add a delay or custom matcher to verify error is cleared
    }
}