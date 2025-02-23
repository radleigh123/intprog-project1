package com.intprog.easeplan

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class RegisterActivity : AppCompatActivity() {
    private lateinit var etFName: EditText
    private lateinit var etLName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button

    private var isAllFieldsChecked = false

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etFName = findViewById<EditText>(R.id.etFName)
        etLName = findViewById<EditText>(R.id.etLName)
        etEmail = findViewById<EditText>(R.id.etEmail)
        etPassword = findViewById<EditText>(R.id.etPassword)
        btnRegister = findViewById<Button>(R.id.btnRegister)

        auth = FirebaseAuth.getInstance()

        btnRegister.setOnClickListener {
            val fName = etFName.text.toString().trim()
            val lName = etLName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            if (fName.isEmpty() || lName.isEmpty()
                || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "All inputs must not be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            Log.i(TAG, "Register Button is clicked")

            if (validateInputs()) {
                registerUser(fName, lName, email, pass)
            }
        }
    }

    private fun registerUser(fName: String, lName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "createUserWithEmail:success")
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    val exception = task.exception

                    if (exception is FirebaseAuthWeakPasswordException) {
                        Toast.makeText(this, "Password too weak. Please use at least 8 characters", Toast.LENGTH_SHORT).show()
                    } else if(exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun validateInputs(): Boolean {
        val fName = etFName.text
        val lName = etLName.text
        val password = etPassword.text

        if (!EMAIL_ADDRESS.matcher(etEmail.text).matches()) {
            etEmail.error = "Email is not valid"
            return false
        }
        if (!isPasswordValid(password.toString())) {
            return false
        }

        return true
    }

    private fun isPasswordValid(pass: String): Boolean {
        if (pass.length < 8 || pass.length > 16) {
            etPassword.error= "Password should be min 8 characters and max 16 characters"
            return false
        }
        if (!pass.any { it.isDigit() }) {
            etPassword.error = "Password should contain at least one digit"
            return false
        }
        if (!pass.any { it.isUpperCase() }) {
            etPassword.error = "Password should contain at least one Capital Letter"
            return false
        }
        if (!pass.any { it.isLowerCase() }) {
            etPassword.error = "Password should contain at least one small Letter"
            return false
        }
        if (!pass.any { !it.isLetterOrDigit() }) {
            etPassword.error = "Password should contain at least one special character"
            return false
        }
        if (pass.any { it.isWhitespace() }) {
            etPassword.error = "Password should not contain any white space"
            return false
        }

        return true
    }
}