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
class SignupInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(signup::class.java)

    @Test
    fun testSignupFormValidation() {
        // Test empty name field
        onView(withId(R.id.btnSignUp)).perform(click())
        onView(withId(R.id.etName)).check(matches(hasErrorText("Please enter your name")))

        // Fill name and test phone validation
        onView(withId(R.id.etName)).perform(typeText("John Doe"))
        onView(withId(R.id.btnSignUp)).perform(click())
        onView(withId(R.id.etPhone)).check(matches(hasErrorText("Please enter your phone number")))

        // Fill phone and test email validation
        onView(withId(R.id.etPhone)).perform(typeText("1234567890"))
        onView(withId(R.id.btnSignUp)).perform(click())
        onView(withId(R.id.etEmail)).check(matches(hasErrorText("Please enter your email")))

        // Fill invalid email
        onView(withId(R.id.etEmail)).perform(typeText("invalid.email"))
        onView(withId(R.id.btnSignUp)).perform(click())
        onView(withId(R.id.etEmail)).check(matches(hasErrorText("Please enter a valid email address")))

        // Fill valid email and test password validation
        onView(withId(R.id.etEmail)).perform(clearText(), typeText("test@example.com"))
        onView(withId(R.id.btnSignUp)).perform(click())
        onView(withId(R.id.etPassword)).check(matches(hasErrorText("Please enter a password")))

        // Fill weak password
        onView(withId(R.id.etPassword)).perform(typeText("password"))
        onView(withId(R.id.btnSignUp)).perform(click())
        onView(withId(R.id.etPassword)).check(matches(hasErrorText("Password must contain at least one number and one letter")))

        // Fill strong password and test confirm password
        onView(withId(R.id.etPassword)).perform(clearText(), typeText("password123"))
        onView(withId(R.id.btnSignUp)).perform(click())
        onView(withId(R.id.etConfirmPassword)).check(matches(hasErrorText("Please confirm your password")))

        // Test mismatched passwords
        onView(withId(R.id.etConfirmPassword)).perform(typeText("password456"))
        onView(withId(R.id.btnSignUp)).perform(click())
        onView(withId(R.id.etConfirmPassword)).check(matches(hasErrorText("Passwords do not match")))
    }

    @Test
    fun testNavigationToLogin() {
        // Click on login text
        onView(withId(R.id.tvLogin)).perform(click())

        // Verify login activity is launched (you'll need to add proper intent verification)
        // This is a basic test - you might want to use Intents.intended() for better verification
    }
}