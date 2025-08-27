package com.example.tarot

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.tarot.utils.JsonUtils
import com.example.tarot.model.TarotMeaning

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)



        val btnAllCards = findViewById<Button>(R.id.btnAllCards)
        val btnRandomReading = findViewById<Button>(R.id.btnRandomReading)

        // Set up button click listeners
        btnAllCards.setOnClickListener {
            // Start AllCardsActivity
            val intent = Intent(this, AllCardsActivity::class.java)
            startActivity(intent)
        }

        btnRandomReading.setOnClickListener {
            // Start RandomReadingActivity
            val intent = Intent(this, RandomReadingActivity::class.java)
            startActivity(intent)
        }

        // In your MainActivity.onCreate()
        val btnAskQuestion = findViewById<Button>(R.id.btnAskQuestion)
        btnAskQuestion.setOnClickListener {
            val intent = Intent(this, QuestionActivity::class.java)
            startActivity(intent)
        }

        // Daily Draw button
        val btnDailyDraw = findViewById<Button>(R.id.btnDailyDraw)
        btnDailyDraw.setOnClickListener {
            val intent = Intent(this, DailyDrawActivity::class.java)
            startActivity(intent)
        }
    }



    }
