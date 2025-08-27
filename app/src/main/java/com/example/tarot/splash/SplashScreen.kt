package com.example.tarot.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tarot.MainActivity
import com.example.tarot.R

class SplashScreen : AppCompatActivity() {
    private lateinit var splashImg: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        splashImg = findViewById(R.id.splashImg)
        splashImg.alpha = 0f
        splashImg.animate().setDuration(1500).alpha(1f).withEndAction {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
            startActivity(Intent(this, MainActivity::class.java))


        }
    }
}