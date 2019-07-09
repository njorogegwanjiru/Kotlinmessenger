package com.example.kotlinmessanger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        back_to_register_text_view
            .setOnClickListener {
                val intent = Intent(this, UserRegistration::class.java)
                startActivity(intent)
            }
    }
}
