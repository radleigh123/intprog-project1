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
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var fillEtFName: com.google.android.material.textfield.TextInputLayout
    private lateinit var fillEtLName: com.google.android.material.textfield.TextInputLayout
    private lateinit var etFName: EditText
    private lateinit var etLName: EditText


    private lateinit var fillEtEmail: com.google.android.material.textfield.TextInputLayout
    private lateinit var etEmail: EditText

    private lateinit var fillEtPass: com.google.android.material.textfield.TextInputLayout
    private lateinit var fillEtPass2: com.google.android.material.textfield.TextInputLayout
    private lateinit var etPassword: EditText
    private lateinit var etPassword2: EditText

    private lateinit var btnRegister: Button

    private lateinit var user: User

    private var isAllFieldsChecked = false

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // TODO: clean
        fillEtFName = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.filledTextFieldFName)
        fillEtLName = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.filledTextFieldLName)
        etFName = findViewById<EditText>(R.id.etFName)
        etLName = findViewById<EditText>(R.id.etLName)

        fillEtEmail = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.filledTextFieldEmail)
        etEmail = findViewById<EditText>(R.id.etEmail)

        fillEtPass = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.filledTextFieldPassword)
        fillEtPass2 = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.filledTextFieldPassword2)
        etPassword = findViewById<EditText>(R.id.etPassword)
        etPassword2 = findViewById<EditText>(R.id.etPassword2)

        btnRegister = findViewById<Button>(R.id.btnRegister)

        auth = FirebaseAuth.getInstance()

        etFName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                fillEtFName.hint = "First Name"
            } else if (etFName.text.isNullOrEmpty()) {
                fillEtFName.hint = "First Name*"
            }
        }

        etLName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                fillEtLName.hint = "Last Name"
            } else if (etLName.text.isNullOrEmpty()) {
                fillEtLName.hint = "Last Name*"
            }
        }

        etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                fillEtEmail.hint = "Email Address"
            } else if (etEmail.text.isNullOrEmpty()) {
                fillEtEmail.hint = "Email Address*"
            }
        }

        etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                fillEtPass.error = null
                fillEtPass2.error = null
            }
        }

        btnRegister.setOnClickListener {
            user = User(
                etFName.text.toString().trim(),
                etLName.text.toString().trim(),
                etEmail.text.toString().trim(),
                etPassword.text.toString().trim()
            )

            if (user._fName.isEmpty() || user._lName.isEmpty()
                || user._email.isEmpty() || user._password.isEmpty()) {
                Toast.makeText(this, "All inputs must not be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            Log.i(TAG, "Register Button is clicked")

            if (validateInputs()) {
                registerUser(user)
            }
        }

    }

    private fun registerUser(user: User) {
        auth.createUserWithEmailAndPassword(user._email, user._password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "createUserWithEmail:success")
                    saveUserToFirestore(auth.currentUser?.uid.toString(), user)
//                    startActivity(Intent(this, LoginActivity::class.java))
//                    finish()
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

    private fun saveUserToFirestore(userId: String, account: User) {
        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "firstName" to account._fName,
            "lastName" to account._lName,
            "email" to account._email,
            "password" to account._password
        )

        db.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "User data saved!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }


    private fun validateInputs(): Boolean {
        val fName = etFName.text
        val lName = etLName.text
        val password = etPassword.text
        val password2 = etPassword2.text

        if (!EMAIL_ADDRESS.matcher(etEmail.text).matches()) {
            etEmail.error = "Email is not valid"
            return false
        }
        if (!isPasswordValid(password.toString(),password2.toString())) {
            return false
        }

        return true
    }

    private fun isPasswordValid(pass: String,pass2: String): Boolean {
        if (pass.length < 8 || pass.length > 16) {
//            etPassword.error= "Password should be min 8 characters and max 16 characters"
            fillEtPass.error = "Password should be min 8 characters and max 16 characters"
            return false
        }
        if (!pass.any { it.isDigit() }) {
//            etPassword.error = "Password should contain at least one digit"
            fillEtPass.error = "Password should contain at least one digit"
            return false
        }
        if (!pass.any { it.isUpperCase() }) {
//            etPassword.error = "Password should contain at least one Capital Letter"
            fillEtPass.error = "Password should contain at least one Capital Letter"
            return false
        }
        if (!pass.any { it.isLowerCase() }) {
//            etPassword.error = "Password should contain at least one small Letter"
            fillEtPass.error = "Password should contain at least one small Letter"
            return false
        }
        if (!pass.any { !it.isLetterOrDigit() }) {
//            etPassword.error = "Password should contain at least one special character"
            fillEtPass.error = "Password should contain at least one special character"
            return false
        }
        if (pass.any { it.isWhitespace() }) {
//            etPassword.error = "Password should not contain any white space"
            fillEtPass.error = "Password should not contain any white space"
            return false
        }
        if (pass != pass2) {
//            etPassword.error = "Password does not match"
            fillEtPass.error = "Password does not match"
            fillEtPass2.error = "Password does not match"
            return false
        }

        return true
    }
}

class User(fName: String, lName: String, email: String, password: String) {
    var _fName: String = fName
    var _lName: String = lName
    var _email: String = email
    var _password: String = password

    override fun toString(): String {
        val str = "Name: $_fName $_lName\n" +
                "Email Address: $_email\n" +
                "Password " + _password
        return str
    }
}