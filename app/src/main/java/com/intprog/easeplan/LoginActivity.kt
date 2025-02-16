package com.intprog.easeplan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editUsername = findViewById<EditText>(R.id.edit_name)
        val editPassword = findViewById<EditText>(R.id.edit_password)

        val btnRegister = findViewById<Button>(R.id.btn_register)
        btnRegister.setOnClickListener {
            Log.e("john", "Button is clicked")
            Log.i("john", "Button is clicked")

            Toast.makeText(this, "Button is closed", Toast.LENGTH_LONG).show()

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val btnLogin = findViewById<Button>(R.id.btn_login)
        btnLogin.setOnClickListener {
            val nameStr = editUsername.text
            val passStr = editPassword.text

            if (nameStr.isNullOrEmpty() || passStr.isNullOrEmpty()) {
                Toast.makeText(this, "Username and Password must not be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
        }

        /*
        * You validate the username and password according to your set username and password
        * This from the server data
        * Or you can have it static*/

    }
}