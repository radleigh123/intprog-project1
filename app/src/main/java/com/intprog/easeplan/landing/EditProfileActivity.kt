package com.intprog.easeplan.landing

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.intprog.easeplan.R

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etfName: TextInputEditText
    private lateinit var etlName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize UI components
        etfName = findViewById(R.id.etfName)
        etlName = findViewById(R.id.etlName)
        etEmail = findViewById(R.id.etEmail)
        btnSave = findViewById(R.id.btnEdit)
        btnCancel = findViewById(R.id.btnCancel)

        // Load user data
        loadUserData()

        // Save button click
        btnSave.setOnClickListener { saveProfileChanges() }

        // Cancel button click
        btnCancel.setOnClickListener { finish() }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        etfName.setText(document.getString("firstName"))
                        etlName.setText(document.getString("lastName"))
                        etEmail.setText(document.getString("email"))
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveProfileChanges() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val updatedData = mapOf(
                "firstName" to etfName.text.toString().trim(),
                "lastName" to etlName.text.toString().trim(),
                "email" to etEmail.text.toString().trim()
            )

            db.collection("users").document(userId).update(updatedData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show()
                }
        }
    }
}