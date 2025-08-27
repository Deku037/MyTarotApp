package com.example.tarot

import android.content.Context
import com.example.tarot.model.TarotMeaning
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TarotMeaningRepository(private val context: Context) {

    private var meanings: Map<String, TarotMeaning> = emptyMap()

    init {
        loadMeanings()
    }

    private fun loadMeanings() {
        try {
            val jsonString = context.assets.open("tarot-meanings.json")
                .bufferedReader()
                .use { it.readText() }

            val type = object : TypeToken<List<TarotMeaning>>() {}.type
            val meaningsList: List<TarotMeaning> = Gson().fromJson(jsonString, type)

            meanings = meaningsList.associateBy { it.name }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getMeaningForCard(cardName: String): TarotMeaning? {
        return meanings[cardName]
    }

    fun getAllMeanings(): Map<String, TarotMeaning> {
        return meanings
    }

    // NEW: Get a concise summary for reading generation
    fun getReadingSnippet(cardName: String): String {
        val meaning = meanings[cardName] ?: return "something significant is unfolding"

        // You can choose which part to use - here I'm using upright meaning
        // You could also randomize between upright/reversed or use keywords
        return if (meaning.meaning_upright.isNotBlank()) {
            meaning.meaning_upright
        } else if (meaning.keywords_upright.isNotEmpty()) {
            meaning.keywords_upright.joinToString(", ")
        } else {
            "something significant is unfolding"
        }
    }
}