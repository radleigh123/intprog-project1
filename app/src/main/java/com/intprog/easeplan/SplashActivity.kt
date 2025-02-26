package com.intprog.easeplan

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.net.Uri
import android.widget.MediaController
import android.os.Looper
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {
    private lateinit var video: VideoView
    private val videoURL = "android.resource://com.intprog.easeplan/${R.raw.splash_animation}"

    override fun onCreate(savedInstanceState: Bundle?) {
        // Loading dark mode
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        video = findViewById<VideoView>(R.id.splash_vid)
        val uri = Uri.parse(videoURL)
        video.setVideoURI(uri)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(video)
        mediaController.setMediaPlayer(video)
//        video.setMediaController(mediaController)
        video.start()

        // Delay for 3 seconds and navigate to LoginActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 500)
    }
}