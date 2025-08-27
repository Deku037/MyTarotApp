package com.example.tarot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tarot.adapters.CategoryAdapter
import com.example.tarot.model.Category

class QuestionActivity : AppCompatActivity() {

    private lateinit var btnHelp: ImageButton
    private lateinit var rvCategories: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        btnHelp = findViewById(R.id.btnHelp)
        rvCategories = findViewById(R.id.rvCategories)

        val categories = listOf(
            Category(R.drawable.ic_love, "Love & Relationships", "love"),
            Category(R.drawable.ic_money, "Finances", "money"),
            Category(R.drawable.ic_career, "Career & Business", "career"),
            Category(R.drawable.ic_health, "Health", "health")
        )

        rvCategories.visibility = View.VISIBLE
        rvCategories.layoutManager = GridLayoutManager(this, 2)
        rvCategories.adapter = CategoryAdapter(categories) { category ->
            val intent = Intent(this, DrawActivity::class.java)
            intent.putExtra("CATEGORY", category.key)
            startActivity(intent)

//        btnHelp.setOnClickListener {
//            rvCategories.visibility = View.VISIBLE
//            rvCategories.layoutManager = GridLayoutManager(this, 2)
//            rvCategories.adapter = CategoryAdapter(categories) { category ->
//                val intent = Intent(this, DrawActivity::class.java)
//                intent.putExtra("CATEGORY", category.key)
//                startActivity(intent)
//            }
//            btnHelp.visibility = View.GONE // hide button after click
//        }
    }
    }
}

