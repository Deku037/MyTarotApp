package com.example.tarot

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.tarot.model.TarotMeaning
import com.example.tarot.utils.JsonUtils
import kotlin.random.Random

class DailyDrawActivity : AppCompatActivity() {

    private lateinit var categoryText: TextView
    private lateinit var summaryText: TextView
    private lateinit var instructionText: TextView
    private lateinit var row1: LinearLayout
    private lateinit var btnInterpret: Button
    private lateinit var loadingLayout: FrameLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var percentText: TextView

    private lateinit var allCards: List<TarotMeaning>
    private lateinit var selectedCards: List<TarotMeaning>
    private val revealedCards = mutableSetOf<Int>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_draw)

        categoryText = findViewById(R.id.dailyTitleText)
        summaryText = findViewById(R.id.dailySummaryText)
        instructionText = findViewById(R.id.dailyInstructionText)
        row1 = findViewById(R.id.dailyRow1)
        btnInterpret = findViewById(R.id.dailyBtnInterpret)
        loadingLayout = findViewById(R.id.dailyLoadingLayout)
        progressBar = findViewById(R.id.dailyLoadingProgress)
        percentText = findViewById(R.id.dailyLoadingPercent)

        loadingLayout.isVisible = false
        summaryText.isVisible = false
        instructionText.isVisible = true
        btnInterpret.isVisible = false

        // Load category
        val category = intent.getStringExtra("CATEGORY") ?: "General"
        categoryText.text = "Daily Reading"

        // Load all cards and pick 3 random
        allCards = JsonUtils.loadTarotCards(this)
        selectedCards = allCards.shuffled(Random(System.currentTimeMillis())).take(1)

        // Show card backs with labels
        showAllCardBacks()

        btnInterpret.setOnClickListener {
            startReadingAnimation()
        }
    }

    private fun showAllCardBacks() {
        row1.removeAllViews()
        val screenWidth = resources.displayMetrics.widthPixels
        val cardWidth = (screenWidth / 3.5).toInt()
        val cardHeight = (cardWidth * 1.5).toInt()
        val labelHeight = 50 // fixed height for labels (adjust as needed)

        val rowLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.CENTER
        }

        for (i in 0 until selectedCards.size) {
            // Vertical layout for card + label
            val cardLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams =
                    LinearLayout.LayoutParams(cardWidth, cardHeight + labelHeight).apply {
                        setMargins(8, 8, 8, 8)
                    }
                gravity = Gravity.CENTER_HORIZONTAL
            }

            // Card back image
            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(cardWidth, cardHeight)
                setImageResource(R.drawable.img)
                scaleType = ImageView.ScaleType.CENTER_CROP
                tag = "back_$i"
                setOnClickListener { revealCard(i, this) }
                alpha = 0f // start invisible
            }

            // Label under card
            val nameLabel = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(cardWidth, labelHeight)
                gravity = Gravity.CENTER
                textSize = 12f
                text = "" // empty initially
                maxLines = 2


            }

            cardLayout.addView(imageView)
            cardLayout.addView(nameLabel)
            rowLayout.addView(cardLayout)

            // Animate card dealing
            animateDealing(imageView, i)
        }

        row1.addView(rowLayout)
    }

    // Animate card without breaking layout alignment
    private fun animateDealing(card: ImageView, index: Int) {
        card.postDelayed({
            card.alpha = 1f
            card.translationY = -300f
            card.animate()
                .translationY(0f)
                .rotationYBy(360f)
                .setDuration(800)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }, (index * 150).toLong())
    }


    private fun revealCard(index: Int, cardBackView: ImageView) {
        if (revealedCards.contains(index)) return
        revealedCards.add(index)

        val flipOut = ScaleAnimation(
            1f, 0f, 1f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply { duration = 400; fillAfter = true }

        val flipIn = ScaleAnimation(
            0f, 1f, 1f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply { duration = 400; fillAfter = true }

        flipOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                val card = selectedCards[index]
                val resId = resources.getIdentifier(
                    card.img?.replace(".jpg", "") ?: "ic_default_card",
                    "drawable",
                    packageName
                )
                cardBackView.setImageResource(if (resId != 0) resId else R.drawable.img)
                cardBackView.startAnimation(flipIn)

                // Update label under card
                val parentLayout = cardBackView.parent as LinearLayout
                val nameLabel = parentLayout.getChildAt(1) as TextView
                nameLabel.text = card.name

                if (revealedCards.size == selectedCards.size) {
                    instructionText.text = "All cards revealed! Tap Interpret to see summary."
                    btnInterpret.isVisible = true
                }
            }
        })

        cardBackView.startAnimation(flipOut)
    }

    private fun startReadingAnimation() {
        btnInterpret.isEnabled = false
        loadingLayout.isVisible = true
        summaryText.isVisible = false
        instructionText.isVisible = false

        var progress = 0
        progressBar.progress = 0
        percentText.text = "0%"

        val handler = android.os.Handler(mainLooper)
        val runnable = object : Runnable {
            override fun run() {
                if (progress < 100) {
                    progress += 5
                    progressBar.progress = progress
                    percentText.text = "$progress%"
                    handler.postDelayed(this, 100)
                } else {
                    percentText.text = "100%"
                    handler.postDelayed({
                        loadingLayout.isVisible = false
                        showReadingSummary()
                        btnInterpret.isEnabled = true
                    }, 300)
                }
            }
        }
        handler.post(runnable)
    }

    private fun showReadingSummary() {
        val summary = generateReadingSummary(selectedCards)
        summaryText.text = summary
        summaryText.isVisible = true
    }

    private fun generateReadingSummary(cards: List<TarotMeaning>): String {
        val sb = StringBuilder()
        val card = cards.first() // Daily draw has only 1 card

        sb.append("🔮 Your Daily Tarot Reading:\n\n")
        sb.append("• **Card: ${card.name}**\n")
        sb.append("${card.meaning_upright ?: "No meaning available."}\n\n")

        // Realistic interpretations (pick one at random)
        val interpretations = listOf(
            "✨ Interpretation: ${card.name} brings an energy that encourages you to reflect deeply on your current circumstances. Observe the patterns in your thoughts and actions, and notice how this guidance may highlight opportunities, challenges, and potential growth. Let it inspire mindful decisions and self-awareness throughout your day.",
            "✨ Interpretation: The presence of ${card.name} signals a chance to pause and connect with your intuition. Its message suggests paying attention to subtle signs around you, understanding underlying emotions, and taking thoughtful action. This guidance supports clarity, resilience, and conscious choice-making.",
            "✨ Interpretation: With ${card.name} influencing your day, consider how its energies affect your relationships, work, or personal growth. Reflect on what you can learn from this insight, and integrate its guidance to navigate obstacles or embrace opportunities with confidence and clarity.",
            "✨ Interpretation: ${card.name} encourages mindful reflection and intentional actions. Let its message help you identify what matters most, evaluate your priorities, and trust your instincts as you move forward. This card's energy can reveal hidden influences and inspire growth in meaningful areas of your life.",
            "✨ Interpretation: The wisdom of ${card.name} invites you to explore your inner landscape. Notice how its guidance resonates with your emotions and thoughts, allowing you to act with awareness, balance, and insight. Use this reflection to make decisions aligned with your personal journey and well-being."
        )

        val randomIndex = Random(System.currentTimeMillis()).nextInt(interpretations.size)
        sb.append(interpretations[randomIndex] + "\n\n")

        sb.append("🔹 Tarot provides guidance, not fixed outcomes. Trust your intuition as you integrate these messages into your day.")

        return sb.toString()
    }



}
