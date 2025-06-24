package com.example.fitnessapp


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class loading_screen : AppCompatActivity() {

    private lateinit var logoContainer: LinearLayout
    private lateinit var tvFitTrack: TextView
    private lateinit var tvTagline: TextView
    private lateinit var tvLoadingScreen: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make it fullscreen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_loading_screen)

        // Hide system UI for immersive experience
        hideSystemUI()

        initViews()
        startAnimations()
    }

    private fun initViews() {
        logoContainer = findViewById(R.id.logo_container)
        tvFitTrack = findViewById(R.id.tv_fit_track)
        tvTagline = findViewById(R.id.tv_tagline)
        tvLoadingScreen = findViewById(R.id.tv_loading_screen)
        progressBar = findViewById(R.id.progress_bar)

        // Set initial states for animation
        logoContainer.alpha = 0f
        logoContainer.scaleX = 0.5f
        logoContainer.scaleY = 0.5f
        tvLoadingScreen.alpha = 0f
        progressBar.alpha = 0f
    }

    private fun startAnimations() {
        // Delay before starting animations
        Handler(Looper.getMainLooper()).postDelayed({
            animateEntrance()
        }, 500)
    }

    private fun animateEntrance() {
        // Loading Screen text fade in
        val loadingTextAnim = ObjectAnimator.ofFloat(tvLoadingScreen, "alpha", 0f, 0.7f).apply {
            duration = 800
            interpolator = DecelerateInterpolator()
        }

        // Logo container entrance animation
        val logoFadeIn = ObjectAnimator.ofFloat(logoContainer, "alpha", 0f, 1f).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
        }

        val logoScaleX = ObjectAnimator.ofFloat(logoContainer, "scaleX", 0.5f, 1.1f, 1f).apply {
            duration = 1200
            interpolator = AccelerateDecelerateInterpolator()
        }

        val logoScaleY = ObjectAnimator.ofFloat(logoContainer, "scaleY", 0.5f, 1.1f, 1f).apply {
            duration = 1200
            interpolator = AccelerateDecelerateInterpolator()
        }

        val logoTranslationY = ObjectAnimator.ofFloat(logoContainer, "translationY", 50f, 0f).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
        }

        // Progress bar fade in
        val progressBarAnim = ObjectAnimator.ofFloat(progressBar, "alpha", 0f, 0.8f).apply {
            duration = 800
            interpolator = DecelerateInterpolator()
            startDelay = 1000
        }

        // Combine animations
        val entranceAnimatorSet = AnimatorSet().apply {
            play(loadingTextAnim).with(logoFadeIn).with(logoScaleX).with(logoScaleY).with(logoTranslationY)
            play(progressBarAnim).after(logoFadeIn)
        }

        entranceAnimatorSet.start()

        // Text shimmer effect for logo
        Handler(Looper.getMainLooper()).postDelayed({
            shimmerEffect()
        }, 1500)

        // Navigate to next activity after delay
        Handler(Looper.getMainLooper()).postDelayed({
            animateExit()
        }, 3500)
    }

    private fun shimmerEffect() {
        val shimmerAnim = ObjectAnimator.ofFloat(tvFitTrack, "alpha", 1f, 0.7f, 1f).apply {
            duration = 1500
            repeatCount = 1
            interpolator = AccelerateDecelerateInterpolator()
        }

        val taglineShimmer = ObjectAnimator.ofFloat(tvTagline, "alpha", 1f, 0.8f, 1f).apply {
            duration = 1500
            repeatCount = 1
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = 200
        }

        val shimmerSet = AnimatorSet().apply {
            play(shimmerAnim).with(taglineShimmer)
        }
        shimmerSet.start()
    }

    private fun animateExit() {
        // Logo zoom and fade out
        val logoScaleUpX = ObjectAnimator.ofFloat(logoContainer, "scaleX", 1f, 1.2f).apply {
            duration = 600
            interpolator = AccelerateDecelerateInterpolator()
        }

        val logoScaleUpY = ObjectAnimator.ofFloat(logoContainer, "scaleY", 1f, 1.2f).apply {
            duration = 600
            interpolator = AccelerateDecelerateInterpolator()
        }

        val logoFadeOut = ObjectAnimator.ofFloat(logoContainer, "alpha", 1f, 0f).apply {
            duration = 600
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = 200
        }

        val loadingTextFadeOut = ObjectAnimator.ofFloat(tvLoadingScreen, "alpha", 0.7f, 0f).apply {
            duration = 400
            interpolator = AccelerateDecelerateInterpolator()
        }

        val progressBarFadeOut = ObjectAnimator.ofFloat(progressBar, "alpha", 0.8f, 0f).apply {
            duration = 400
            interpolator = AccelerateDecelerateInterpolator()
        }

        val exitAnimatorSet = AnimatorSet().apply {
            play(logoScaleUpX).with(logoScaleUpY).with(logoFadeOut)
            play(loadingTextFadeOut).with(progressBarFadeOut).before(logoFadeOut)
        }

        exitAnimatorSet.start()

        // Navigate to main activity
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToMainActivity()
        }, 800)
    }

    private fun navigateToMainActivity() {
        try {
            val intent = Intent(this, access::class.java)
            startActivity(intent)

            // Custom transition animation
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            // If MainActivity doesn't exist, just finish the splash
            finish()
        }
    }

    private fun hideSystemUI() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.logo_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //override fun onBackPressed() {
        // Disable back button during splash screen
        // super.onBackPressed() - commented out to prevent going back
    //}
}