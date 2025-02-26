package com.intprog.easeplan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.preference.Preference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.intprog.easeplan.landing.EditProfileActivity

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var btnEdit: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        btnEdit = view.findViewById<Button>(R.id.btnEdit)

        val tvEmail = view.findViewById<TextView>(R.id.tv_email)
        val tvFirstName = view.findViewById<TextView>(R.id.tv_fName)
        val tvLastName = view.findViewById<TextView>(R.id.tv_lName)

        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        tvEmail.text = document.getString("email") ?: "N/A"
                        tvFirstName.text = document.getString("firstName") ?: "N/A"
                        tvLastName.text = document.getString("lastName") ?: "N/A"
                    } else {
                        Log.d("Firestore", "No user data found!")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error fetching user data", e)
                }
        }

        btnEdit.setOnClickListener {
            Log.d("Profile", "Edit was clicked")
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        return view
    }
}