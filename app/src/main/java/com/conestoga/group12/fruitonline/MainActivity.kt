package com.conestoga.group12.fruitonline

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var isNavigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logo = findViewById<ImageView>(R.id.logo)

        Handler().postDelayed({ this.navigateToLogin() }, 3000)

        logo.setOnClickListener { v: View? -> navigateToLogin() }
    }

    private fun navigateToLogin() {
        if (!isNavigated) {
            isNavigated = true
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}