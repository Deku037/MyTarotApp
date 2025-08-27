package com.example.tarot.utils

import android.content.Context
import android.util.Log
import com.example.tarot.model.TarotData
import com.example.tarot.model.TarotMeaning
import com.example.tarot.model.QuestionCategory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonUtils {

    // Load Tarot cards with full meanings
    fun loadTarotCards(context: Context): List<TarotMeaning> {
        return try {
            Log.d("JSON_DEBUG", "Loading tarot-meanings.json...")

            val jsonString = context.assets.open("tarot-meanings.json")
                .bufferedReader()
                .use { it.readText() }

            Log.d("JSON_DEBUG", "JSON loaded successfully! Length: ${jsonString.length} chars")

            val tarotData = Gson().fromJson(jsonString, TarotData::class.java)

            Log.d("JSON_DEBUG", "Parsed ${tarotData.cards.size} cards with images")
            if (tarotData.cards.isNotEmpty()) {
                val first5 = tarotData.cards.take(5).joinToString { "${it.name} (${it.img})" }
                Log.d("JSON_DEBUG", "First 5 cards with images: $first5")
            }

            tarotData.cards

        } catch (e: Exception) {
            Log.e("JSON_DEBUG", "ERROR loading tarot-meanings.json: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    // Load Question Categories from JSON
    fun loadCategories(context: Context): List<QuestionCategory> {
        return try {
            Log.d("JSON_DEBUG", "Loading categories.json...")

            val jsonString = context.assets.open("categories.json")
                .bufferedReader()
                .use { it.readText() }

            val listType = object : TypeToken<List<QuestionCategory>>() {}.type
            val categories: List<QuestionCategory> = Gson().fromJson(jsonString, listType)

            Log.d("JSON_DEBUG", "Parsed ${categories.size} categories")
            categories

        } catch (e: Exception) {
            Log.e("JSON_DEBUG", "ERROR loading categories.json: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}
