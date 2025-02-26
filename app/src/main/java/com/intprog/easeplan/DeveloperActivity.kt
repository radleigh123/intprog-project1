package com.intprog.easeplan

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DeveloperActivity : AppCompatActivity() {
    private lateinit var backBtn: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

        backBtn = findViewById(R.id.backBtn)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val tvDev = findViewById<TextView>(R.id.developer_name)

        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val fName = document.getString("firstName") ?: "N/A"
                        val lName = document.getString("lastName") ?: "N/A"
                        tvDev.text = "$fName $lName"
                    } else {
                        Log.d("Firestore", "No user data found!")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error fetching user data", e)
                }
        }

        backBtn.setOnClickListener {
           finish()
        }
    }
}