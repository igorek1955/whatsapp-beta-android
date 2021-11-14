package com.example.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.Toast

import android.text.TextUtils
import com.google.firebase.FirebaseApp


class RegisterActivity : AppCompatActivity() {
    //widgets
    lateinit var userNameTextView: EditText
    lateinit var passwordTextView: EditText
    lateinit var emailTextView: EditText
    lateinit var registerButton: Button

    //not widgets
    lateinit var dbRef: DatabaseReference
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initVals()
        setupButton()
    }

    private fun setupButton() {
        registerButton.setOnClickListener {
            val username: String = userNameTextView.text.toString()
            val email: String = emailTextView.text.toString()
            val password: String = passwordTextView.text.toString()

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(
                    password
                )
            ) {
                Toast.makeText(this@RegisterActivity, "Please Fill All Fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                registerNow(username, password, email)
            }
        }
    }

    private fun initVals() {
        userNameTextView = findViewById(R.id.edit_text_username)
        passwordTextView = findViewById(R.id.edit_text_password)
        emailTextView = findViewById(R.id.edit_text_email)
        registerButton = findViewById(R.id.button_register)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun registerNow(username: String, password: String, email: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val userId = firebaseAuth.currentUser?.uid.toString()
                val userDetails: HashMap<String, String> = HashMap()
                userDetails.put("id", userId)
                userDetails.put("username", username);
                userDetails.put("imageUrl", "default")
                userDetails.put("status", "offline")

                dbRef = FirebaseDatabase.getInstance().getReference("MyUsers")
                    .child(userId);
                dbRef.setValue(userDetails).addOnCompleteListener {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }else{
                Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
            }
        }
    }
}