package com.example.whatsappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser


class LoginActivity : AppCompatActivity() {
    //widgets
    lateinit var usernameET: EditText
    lateinit var passwordET: EditText
    lateinit var registerBtn: Button
    lateinit var loginBtn: Button

    //firebase auth
    lateinit var auth: FirebaseAuth
    var firebaseUser: FirebaseUser? = null

    override fun onStart() {
        super.onStart()

        auth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(applicationContext)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initWidgets()
        setupButtons()
    }

    private fun setupButtons() {
        loginBtn.setOnClickListener {
            val login = usernameET.text.toString()
            val password = passwordET.text.toString()
            if (TextUtils.isEmpty(login) && TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT).show()
            } else {
                processLoginData(login, password)
            }
        }

        registerBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun processLoginData(login: String, password: String) {
        auth.signInWithEmailAndPassword(login, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val i = Intent(this, MainActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
                finish()
            } else {
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initWidgets() {
        usernameET = findViewById(R.id.edit_text_username_login)
        passwordET = findViewById(R.id.edit_text_password_login)
        registerBtn = findViewById(R.id.button_register_loginScreen)
        loginBtn = findViewById(R.id.button_login)
    }
}