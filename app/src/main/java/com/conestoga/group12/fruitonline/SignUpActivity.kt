package com.conestoga.group12.fruitonline

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.conestoga.group12.fruitonline.utils.ValidateUtils.isValidEmail
import com.conestoga.group12.fruitonline.utils.ValidateUtils.isValidPassword
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import java.util.Objects

class SignUpActivity : AppCompatActivity() {
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var btnSignUp: Button

    private var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        firebaseAuth = FirebaseAuth.getInstance()

        emailField = findViewById(R.id.emailField)
        passwordField = findViewById(R.id.passwordField)
        btnSignUp = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener { v: View? ->
            val email = emailField.getText().toString().trim { it <= ' ' }
            val password = passwordField.getText().toString().trim { it <= ' ' }

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this@SignUpActivity, "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                Toast.makeText(
                    this@SignUpActivity,
                    "Invalid email address. Please enter a valid email.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!isValidPassword(password)) {
                Toast.makeText(
                    this@SignUpActivity,
                    "Password must be at least 8 characters long, include a number, an uppercase letter, a lowercase letter, and a special character.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            firebaseAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Registration Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e(
                            TAG,
                            "Registration Failed: " + (Objects.requireNonNull(task.exception)?.message
                                ?: "")
                        )
                        Toast.makeText(
                            this@SignUpActivity,
                            "Registration Failed: " + task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    companion object {
        private const val TAG = "SignUpActivity"
    }
}