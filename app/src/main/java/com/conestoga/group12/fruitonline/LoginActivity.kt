package com.conestoga.group12.fruitonline

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import java.util.Objects

class LoginActivity : AppCompatActivity() {

    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var btnLogin: Button
    private lateinit var signUpPrompt: TextView

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        emailField = findViewById(R.id.emailField)
        passwordField = findViewById(R.id.passwordField)

        btnLogin = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener { v: View? -> loginUser() }

        signUpPrompt = findViewById(R.id.signUpPrompt)
        signUpPrompt.setOnClickListener { v: View? ->
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        val email = intent.getStringExtra("email")
        if (!TextUtils.isEmpty(email)) {
            emailField.setText(email)
        }
    }

    private fun loginUser() {
        val email = emailField.text.toString().trim { it <= ' ' }
        val password = passwordField.text.toString().trim { it <= ' ' }

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val intent = Intent(this@LoginActivity, ProductActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e(TAG, "Login failed: " + (Objects.requireNonNull(task.exception)?.message
                        ?: ""))
                    Toast.makeText(
                        this,
                        "Login failed: " + task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}