package com.intprog.easeplan

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var auth: FirebaseAuth

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        auth = FirebaseAuth.getInstance()

        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<Preference>("developer")
            ?.setOnPreferenceClickListener {
                Log.d("Preferences", "Developer was clicked")
                startActivity(Intent(requireContext(), DeveloperActivity::class.java))
                true
            }

        findPreference<Preference>("logOut")
            ?.setOnPreferenceClickListener {
                Log.d("logOut", "logOut was clicked")

                AlertDialog.Builder(requireContext())
                    .setTitle("Sign Out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Yes") { _, _ ->
                        auth.signOut()
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()

                true
            }

        val darkModeSwitch = findPreference<SwitchPreferenceCompat>("theme_dark")
        darkModeSwitch?.setOnPreferenceChangeListener { _, newValue ->
            val isDarkMode = newValue as Boolean
            setDarkMode(isDarkMode)
            true
        }

    }

    private fun setDarkMode(enabled: Boolean) {
        val nightMode = if (enabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)

        // Save preference
        val sharedPreferences = requireContext().getSharedPreferences("AppPrefs", android.content.Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("dark_mode", enabled).apply()
    }

}