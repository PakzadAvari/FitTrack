package com.example.fitnessapp


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat

class access : AppCompatActivity() {

    private lateinit var signUpCard: CardView
    private lateinit var loginText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_access)

        // Make status bar transparent
        setupStatusBar()

        // Initialize views
        initViews()

        // Setup click listeners
        setupClickListeners()

        // Setup animations
        setupAnimations()
    }

    private fun setupStatusBar() {
        window.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            statusBarColor = ContextCompat.getColor(this@access, android.R.color.transparent)
        }
    }

    private fun initViews() {
        signUpCard = findViewById(R.id.signUpCard)
        loginText = findViewById(R.id.loginText)
    }

    private fun setupClickListeners() {
        // Sign Up button click
        signUpCard.setOnClickListener {
            handleSignUpClick()
        }

        // Login text click
        loginText.setOnClickListener {
            handleLoginClick()
        }
    }

    private fun setupAnimations() {
        // Animate sign up card entrance
        signUpCard.apply {
            alpha = 0f
            translationY = 100f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .setStartDelay(300)
                .start()
        }

        // Animate login text entrance
        loginText.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay(500)
                .start()
        }
    }

    private fun handleSignUpClick() {
        // Add scale animation for button press feedback
        signUpCard.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                signUpCard.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()

        // Navigate to sign up form or handle sign up logic
        navigateToSignUpForm()
    }

    private fun handleLoginClick() {
        // Add subtle animation for login text
        loginText.animate()
            .alpha(0.7f)
            .setDuration(100)
            .withEndAction {
                loginText.animate()
                    .alpha(1f)
                    .setDuration(100)
                    .start()
            }
            .start()

        // Navigate to login screen
        navigateToLogin()
    }

    private fun navigateToSignUpForm() {
        // Replace with your actual sign up form activity
        val intent = Intent(this, signup::class.java)
        startActivity(intent)

        // Add custom transition animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun navigateToLogin() {
        // Replace with your actual login activity
        val intent = Intent(this, login::class.java)
        startActivity(intent)

        // Add custom transition animation
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Add custom exit animation
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}