package com.aldi.storyappdicoding.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aldi.storyappdicoding.R

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        supportActionBar?.hide()
    }
}