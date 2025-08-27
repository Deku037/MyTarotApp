package com.example.tarot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tarot.model.TarotCard
import com.example.tarot.model.TarotMeaning
import com.example.tarot.utils.JsonUtils

class AllCardsActivity : AppCompatActivity() {

    private lateinit var allCards: List<TarotMeaning>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_cards)

        val container = findViewById<LinearLayout>(R.id.cardsContainer)
        val titleText = findViewById<TextView>(R.id.titleText)

        // Load all cards from JSON
        allCards = JsonUtils.loadTarotCards(this)
        titleText.text = "All Tarot Cards (${allCards.size})"

        // Show all cards in grid
        createCardGrid(container, allCards, 4)
    }

    private fun createCardGrid(container: LinearLayout, cards: List<TarotMeaning>, cardsPerRow: Int) {
        val totalRows = (cards.size + cardsPerRow - 1) / cardsPerRow

        for (row in 0 until totalRows) {
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                weightSum = cardsPerRow.toFloat()
            }

            for (col in 0 until cardsPerRow) {
                val index = row * cardsPerRow + col
                if (index < cards.size) {
                    val card = cards[index]
                    val cardView = createCardView(card)

                    val layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        weight = 1f
                        setMargins(4, 4, 4, 4)
                    }

                    cardView.layoutParams = layoutParams
                    rowLayout.addView(cardView)
                }
            }

            container.addView(rowLayout)
        }
    }

    private fun createCardView(card: TarotMeaning): View {
        val cardLayout = layoutInflater.inflate(R.layout.item_card, null)

        val cardImage = cardLayout.findViewById<ImageView>(R.id.cardImage)
        val cardName = cardLayout.findViewById<TextView>(R.id.cardName)
        val cardDetails = cardLayout.findViewById<TextView>(R.id.cardDetails)

        // Set card text
        cardName.text = card.name
        val details = "#${card.number}"
        cardDetails.text = if (card.suit != null) {
            "$details • ${card.suit}"
        } else {
            "$details • ${card.arcana}"
        }

        // Load image
        if (!card.img.isNullOrEmpty()) {
            val resourceId = resources.getIdentifier(
                card.img.replace(".jpg", ""),
                "drawable",
                packageName
            )
            if (resourceId != 0) {
                cardImage.setImageResource(resourceId)
            }
        }

        // Click listener for card details
        cardLayout.setOnClickListener {
            val intent = Intent(this, CardDetailActivity::class.java).apply {
                putExtra("CARD_NAME", card.name)
                putExtra("CARD_IMAGE", card.img)
                putExtra("CARD_NUMBER", card.number)
                putExtra("CARD_ARCANA", card.arcana)
                putExtra("CARD_SUIT", card.suit ?: "")
            }
            startActivity(intent)
        }

        return cardLayout
    }
}