package com.example.expenseshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val intentToAboutActivity: Intent = getIntent()
        val name = intentToAboutActivity.getStringExtra("name").toString()
        val email = intentToAboutActivity.getStringExtra("email").toString()

        findViewById<TextView>(R.id.welcomeText).text = getString(R.string.Welcome, name)
        findViewById<Button>(R.id.backButton).setOnClickListener()
        {
            val intentToHomeActivity:Intent = Intent(this, HomeActivity::class.java)
            intentToHomeActivity.putExtra("name", name)
            intentToHomeActivity.putExtra("email", email)
            startActivity(intentToHomeActivity)
        }
    }
}