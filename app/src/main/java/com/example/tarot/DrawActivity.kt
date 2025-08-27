package com.example.tarot

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

class DrawActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw)

        categoryText = findViewById(R.id.drawTitleText)
        summaryText = findViewById(R.id.drawSummaryText)
        instructionText = findViewById(R.id.drawInstructionText)
        row1 = findViewById(R.id.drawRow1)
        btnInterpret = findViewById(R.id.drawBtnInterpret)
        loadingLayout = findViewById(R.id.drawLoadingLayout)
        progressBar = findViewById(R.id.drawLoadingProgress)
        percentText = findViewById(R.id.drawLoadingPercent)

        loadingLayout.isVisible = false
        summaryText.isVisible = false
        instructionText.isVisible = true
        btnInterpret.isVisible = false

        // Load category
        val category = intent.getStringExtra("CATEGORY") ?: "General"
        categoryText.text = "Category: $category"

        // Load all cards and pick 3 random
        allCards = JsonUtils.loadTarotCards(this)
        selectedCards = allCards.shuffled(Random(System.currentTimeMillis())).take(3)

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
                layoutParams = LinearLayout.LayoutParams(cardWidth, cardHeight + labelHeight).apply {
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
                val resId = resources.getIdentifier(card.img?.replace(".jpg", "") ?: "ic_default_card", "drawable", packageName)
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
                        showReadingSummary(selectedCards, categoryText.text.toString().replace("Category: ", "").trim())
                        summaryText.isVisible = true
                        btnInterpret.isEnabled = true
                    }, 300)
                }
            }
        }
        handler.post(runnable)
    }


    private fun showReadingSummary(cards: List<TarotMeaning>, category: String) {
        val sb = StringBuilder()
        val random = Random(System.currentTimeMillis())

        sb.append("🔮 Your ${category.capitalize()} Tarot Reading:\n\n")

        // Iterate cards with category-specific narrative
        cards.forEach { card ->
            val meaning = when {
                card.advice.isNotBlank() -> card.advice
                card.meaning_upright.isNotBlank() -> card.meaning_upright
                card.keywords_upright.isNotEmpty() -> card.keywords_upright.joinToString(", ")
                card.meaning_reversed.isNotBlank() -> card.meaning_reversed
                card.keywords_reversed.isNotEmpty() -> card.keywords_reversed.joinToString(", ")
                else -> if (card.arcana == "Major Arcana") "represents a significant life lesson"
                else "affects ${card.suit?.lowercase() ?: "your life"}"
            }

            val narrative = when (category.lowercase()) {
                "love" -> listOf(
                    "${card.name} emerges with a vivid presence, drawing you into the intricate tapestry of emotions and connections surrounding the heart. Its imagery is alive with gestures, colors, and subtle symbols that whisper stories of longing, joy, and reflection: $meaning.",
                    "With ${card.name}, the dynamics of relationships come alive in its rich imagery and textures. The card seems to capture past experiences, present emotions, and the subtle threads that weave connections: $meaning.",
                    "${card.name} radiates energy and nuance, painting a scene where emotion, imagination, and perception intertwine. Its details evoke the rhythms of the heart and the unseen currents shaping love: $meaning."
                ).random()

                "career" -> listOf(
                    "${card.name} presents a landscape of ambition, effort, and opportunity. Its imagery evokes the flow of projects, decisions, and collaborations, bringing the professional world vividly to life: $meaning.",
                    "With ${card.name}, the patterns of work and growth are illuminated. Colors, lines, and figures hint at challenges faced, achievements earned, and paths unfolding: $meaning.",
                    "${card.name} carries energy of focus, transition, and momentum, suggesting the rhythms and cycles of professional life and the journey toward your goals: $meaning."
                ).random()

                "health" -> listOf(
                    "${card.name} portrays vitality, energy, and the present state of body and mind. The imagery evokes balance, awareness, and the ongoing cycles of wellbeing: $meaning.",
                    "With ${card.name}, subtle cues of self-care, recovery, and mindfulness come to the forefront. Its figures and symbols suggest the rhythm of daily life and health practices: $meaning.",
                    "${card.name} radiates resilience and restoration, highlighting the interplay of physical, mental, and emotional health in a vivid and thoughtful way: $meaning."
                ).random()

                "finance" -> listOf(
                    "${card.name} depicts the flow of resources, accumulation, and cycles of effort and reward. The imagery evokes balance, opportunity, and the subtle currents of financial life: $meaning.",
                    "With ${card.name}, choices, timing, and unseen influences on resources are illustrated in rich detail. Its symbols hint at growth, strategy, and the ebb and flow of material energy: $meaning.",
                    "${card.name} carries a calm yet active presence, portraying the rhythms of wealth, management, and planning in a way that feels alive: $meaning."
                ).random()

                else -> listOf(
                    "${card.name} radiates vivid imagery, textures, and story, inviting reflection and curiosity: $meaning.",
                    "The details of ${card.name} feel alive, layered with symbolism and subtle energy, encouraging observation and thought: $meaning.",
                    "With ${card.name}, hidden currents, forms, and textures spark imagination and invite engagement with its story: $meaning."
                ).random()
            }

            sb.append("• $narrative\n\n")
        }



        // 5 possible reflections per category, now with at least two sentences
        val reflections = when (category.lowercase()) {
            "love" -> listOf(
                "💖 These messages encourage deep emotional honesty and connection with others. Take time to understand your own needs as well as those of your partner or potential love interest.",
                "💖 Reflect on how past experiences shape your current relationships. Awareness of these patterns can help you nurture healthier connections moving forward.",
                "💖 Trust in your intuition and act with compassion in matters of the heart. Small gestures of understanding and empathy can create meaningful bonds.",
                "💖 Be mindful of balance between giving and receiving love. Mutual respect and open communication will guide your romantic path effectively.",
                "💖 Openness, patience, and self-awareness will guide your romantic path. Allow yourself to experience love fully while maintaining your personal boundaries."
            )
            "career" -> listOf(
                "💼 These cards suggest evaluating opportunities with both pragmatism and vision. Consider how each choice aligns with your long-term goals and personal growth.",
                "💼 Focus on personal growth while navigating professional challenges. Learning from obstacles can strengthen your skills and prepare you for advancement.",
                "💼 Strategic planning and adaptability are key to long-term success. Remain flexible to changes while staying committed to your overarching objectives.",
                "💼 Recognize strengths and leverage them in your career decisions. Collaborative efforts and networking can enhance outcomes significantly.",
                "💼 Reflect on what truly aligns with your ambitions and values. Making decisions that honor both your skills and passions will yield greater fulfillment."
            )
            "health" -> listOf(
                "🧘‍♀️ Prioritize self-care and listen to your body and mind carefully. Consistent routines and mindfulness practices can enhance your overall wellbeing.",
                "🧘‍♀️ Small consistent actions lead to sustainable wellbeing. Incorporating rest, nutrition, and exercise thoughtfully will support lasting health benefits.",
                "🧘‍♀️ Consider both physical activity and mental rest for balance. Pay attention to emotional signals as they often reflect deeper needs of the body.",
                "🧘‍♀️ Mindfulness and reflection can enhance health decisions. Take time to check in with yourself regularly and adjust habits that no longer serve you.",
                "🧘‍♀️ Adapt your habits to promote harmony in body and mind. Balance between work, rest, and leisure ensures optimal energy and mental clarity."
            )
            "finance" -> listOf(
                "💰 Plan carefully while considering both short-term and long-term goals. Thoughtful budgeting and foresight will help you navigate financial challenges.",
                "💰 Be deliberate with resources to maximize opportunities. Recognize where investments, savings, or expenditures align with your priorities.",
                "💰 Reflection and foresight are essential for financial stability. Evaluate risks and rewards to make informed decisions that support your goals.",
                "💰 Evaluate options, mitigate risks, and align spending with priorities. Smart planning and careful observation of trends can lead to steady growth.",
                "💰 Use these insights to make informed and strategic financial choices. Balance ambition with caution to maintain security while pursuing opportunities."
            )
            else -> listOf(
                "🔹 Reflect deeply on these messages and allow your intuition to guide you. Consider how each insight applies to your current life circumstances.",
                "🔹 Consider how these insights apply to your current life situation. Take time to interpret the guidance thoughtfully and integrate it into your plans.",
                "🔹 Take time to integrate this guidance thoughtfully. Your reflection will help transform these messages into actionable steps.",
                "🔹 These cards offer perspective on your path forward. Combining intuition with practical action will yield the best outcomes.",
                "🔹 Trust your instincts as you navigate upcoming decisions. Observe patterns and lessons that emerge to guide your choices wisely."
            )
        }


        // Pick one randomly
        val selectedReflection = reflections[random.nextInt(reflections.size)]
        sb.append("✨ Reflection: $selectedReflection\n\n")
        sb.append("🔹 Remember: Tarot provides guidance, not fixed outcomes. Trust your intuition as you interpret these messages.")

        summaryText.text = sb.toString()
        summaryText.isVisible = true
    }




}
