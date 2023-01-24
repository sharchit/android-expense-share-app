package com.example.expenseshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        val intentToProfileActivity: Intent = getIntent()
        val email = intentToProfileActivity.getStringExtra("email").toString()
        val name = intentToProfileActivity.getStringExtra("name").toString()
        findViewById<TextView>(R.id.NameTextView).text = getString(R.string.Name, name)
        findViewById<TextView>(R.id.EmailTextView).text = getString(R.string.Email, email)

        findViewById<Button>(R.id.backButton).setOnClickListener()
        {
            val intentToHomeActivity:Intent = Intent(this, HomeActivity::class.java)
            intentToHomeActivity.putExtra("name", name)
            intentToHomeActivity.putExtra("email", email)
            startActivity(intentToHomeActivity)
        }
        findViewById<Button>(R.id.signOutButton).setOnClickListener()
        {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}