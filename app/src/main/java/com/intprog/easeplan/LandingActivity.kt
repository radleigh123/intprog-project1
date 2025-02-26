package com.intprog.easeplan

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class LandingActivity : AppCompatActivity() {
    private lateinit var vid1: VideoView
    private val videoURL = "android.resource://com.intprog.easeplan/${R.raw.facebook2}"

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        vid1 = findViewById<VideoView>(R.id.vid1)
        val uri = Uri.parse(videoURL)
        vid1.setVideoURI(uri)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(vid1)
        mediaController.setMediaPlayer(vid1)
        vid1.setMediaController(mediaController)
//        vid1.start()

        vid1.setOnPreparedListener { mp -> mp.isLooping = true; };

        auth = FirebaseAuth.getInstance()
//        auth.signOut()

        if(auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.itemIconTintList = null // WORKAROUND overriding to null to show icon's original color

        val defaultFragment = DefaultFragment()
        val profileFragment = ProfileFragment()
        val settingsFragment = SettingsFragment()

        setCurrentFragment(defaultFragment)

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> setCurrentFragment(defaultFragment)
                R.id.profile -> setCurrentFragment(profileFragment)
                R.id.settings -> setCurrentFragment(settingsFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_container, fragment)
            commit()
        }

}