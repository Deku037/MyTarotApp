// TarotCard.kt
package com.example.tarot.model

data class TarotCard(
    val name: String,
    val number: String,
    val arcana: String,
    val suit: String? = null,
    val img: String? = null  // ADD THIS
)

data class TarotData(
    val description: String,
    val cards: List<TarotMeaning>
)

data class TarotMeaning(
    val name: String,
    val number: String,
    val arcana: String,
    val suit: String?,
    val img: String?,

    // Add these new fields for meanings
    val keywords_upright: List<String> = emptyList(),
    val keywords_reversed: List<String> = emptyList(),
    val meaning_upright: String = "",
    val meaning_reversed: String = "",
    val advice: String = ""
)

data class QuestionCategory(
    val id: String,
    val name: String,
    val emoji: String,
    val keywords: List<String>,
    val cardFocus: List<String> // Which cards are more relevant for this category
)

object QuestionCategories {
    val categories = listOf(
        QuestionCategory(
            id = "relationship",
            name = "Relationship & Love",
            emoji = "💕",
            keywords = listOf("love", "relationship", "partner", "marriage", "dating", "romance", "family", "friendship"),
            cardFocus = listOf("Cups", "The Lovers", "Two of Cups", "Ten of Cups", "The Empress", "The Emperor")
        ),
        QuestionCategory(
            id = "career",
            name = "Career & Work",
            emoji = "💼",
            keywords = listOf("job", "career", "work", "business", "promotion", "money", "finance", "success", "office"),
            cardFocus = listOf("Pentacles", "Wands", "The Chariot", "The World", "Ace of Pentacles", "Three of Pentacles")
        ),
        QuestionCategory(
            id = "health",
            name = "Health & Wellness",
            emoji = "🌿",
            keywords = listOf("health", "wellness", "healing", "recovery", "body", "mind", "spirit", "fitness", "diet"),
            cardFocus = listOf("The Star", "Temperance", "The Hermit", "Four of Swords", "Ace of Cups", "Nine of Cups")
        ),
        QuestionCategory(
            id = "spiritual",
            name = "Spiritual Growth",
            emoji = "✨",
            keywords = listOf("spirit", "purpose", "meaning", "growth", "journey", "path", "destiny", "intuition", "guidance"),
            cardFocus = listOf("Major Arcana", "The Fool", "The High Priestess", "The Moon", "Judgement", "The World")
        ),
        QuestionCategory(
            id = "general",
            name = "General Guidance",
            emoji = "🔮",
            keywords = listOf(),
            cardFocus = listOf() // All cards
        )
    )

    fun detectCategory(question: String): QuestionCategory {
        val lowercaseQuestion = question.lowercase()
        return categories.find { category ->
            category.keywords.any { keyword ->
                lowercaseQuestion.contains(keyword, ignoreCase = true)
            }
        } ?: categories.last() // default to general
    }
}

data class Category(
    val iconRes: Int,
    val name: String,
    val key: String
)