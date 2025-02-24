package com.intprog.easeplan

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns.EMAIL_ADDRESS
import android.view.View
import android.widget.AutoCompleteTextView.Validator
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var tbForgotPass: Button

    // For validation purposes on the inputs (username and password)
    private var isAllFieldsChecked = false

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        etEmail = findViewById<EditText>(R.id.etEmail)
        etPassword = findViewById<EditText>(R.id.etPassword)

        btnLogin = findViewById<Button>(R.id.btnLogin)
        btnRegister = findViewById<Button>(R.id.btnRegister)
        tbForgotPass = findViewById<Button>(R.id.tbForgotPass)

        btnLogin.setOnClickListener {
            if (etEmail.text.isNullOrEmpty() || etPassword.text.isNullOrEmpty()) {
                Toast.makeText(this, "Username or Password must not be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            Log.i("john", "Login Button is clicked")
            isAllFieldsChecked = validateInputs()

            loginUser(etEmail.text.toString(), etPassword.text.toString())
        }

        btnRegister.setOnClickListener {
            Log.i("john", "Register Button is clicked")
//            Toast.makeText(this, "Button is closed", Toast.LENGTH_LONG).show()

            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)
        }

        tbForgotPass.setOnClickListener {
            Log.i("john", "Forgot Password Text Button is clicked")

            val i = Intent(this, RegisterActivity::class.java) // TODO: ADD FORGOTPASSWORDACTIVITY
            startActivity(i)
        }


    }

    public override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(account: FirebaseUser?){
        if(account != null){
            Log.i(TAG, "updateUI method result: account is already signed in")
            Toast.makeText(this,"You Signed In successfully",Toast.LENGTH_LONG).show();
            val i = Intent(this, LandingActivity::class.java)
            startActivity(i)
        }else {
            Log.i(TAG, "updateUI method result: account is null")
//            Toast.makeText(this,"You Didnt signed in",Toast.LENGTH_LONG).show();
        }
    }

    private fun validateInputs(): Boolean {
        val password = etPassword.text
        val emailLayout = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.textInputLayoutEmail)

        if (!EMAIL_ADDRESS.matcher(etEmail.text).matches()) {
            emailLayout.error = "Email is not valid"

            return false
        }

        return true
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Welcome, ${user?.email}!", Toast.LENGTH_LONG).show()
                    updateUI(user)
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed: Account does not exist", Toast.LENGTH_LONG).show()
                }
            }
    }
}