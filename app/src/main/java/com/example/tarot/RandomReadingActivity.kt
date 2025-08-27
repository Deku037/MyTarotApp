package com.example.tarot

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.tarot.model.TarotCard
import com.example.tarot.model.TarotMeaning
import com.example.tarot.utils.JsonUtils

class RandomReadingActivity : AppCompatActivity() {

    private lateinit var allCards: List<TarotMeaning>
    private lateinit var randomCards: List<TarotMeaning>
    private lateinit var btnInterpret: Button
    private lateinit var summaryText: TextView
    private lateinit var instructionText: TextView
    private lateinit var row1: LinearLayout
    private lateinit var row2: LinearLayout
    private var revealedCards = mutableSetOf<Int>()
    private lateinit var loadingLayout: FrameLayout
    private lateinit var loadingText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_reading)

        // Initialize views
        row1 = findViewById(R.id.row1)
        row2 = findViewById(R.id.row2)

        btnInterpret = findViewById(R.id.btnInterpret)
        summaryText = findViewById(R.id.summaryText)
        instructionText = findViewById(R.id.instructionText)


        // Load all cards from JSON
         allCards = JsonUtils.loadTarotCards(this)


        // Get 5 random cards
        randomCards = allCards.shuffled().take(5)
//        titleText.text = "Your Reading (5 Cards)"

        showTarotIntroDialog()

        // Show all 5 card backs initially
//        showAllCardBacks()

        loadingLayout = findViewById(R.id.loadingLayout)
        loadingLayout.isVisible = false
        loadingText = findViewById(R.id.loadingText)
        loadingText.isVisible = false

        // Set up interpret button
        btnInterpret.setOnClickListener {
            startReadingAnimation()

        }
    }

    private fun showTarotIntroDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_tarot_intro, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // must tap button to continue
            .create()

        val btnStart = dialogView.findViewById<Button>(R.id.btnStartReading)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvIntroMessage)
        animateBreathingMessage(tvMessage)

        tvMessage.text = "Take a deep breath and focus on your wish. When ready, tap Start."

        btnStart.setOnClickListener {
            dialog.dismiss()
            val titleText = findViewById<TextView>(R.id.titleText)
            titleText.text = "Your Reading (5 Cards)"
            instructionText.text = "Tap each card to reveal its meaning."
            showAllCardBacks()  // start showing card backs after dialog
        }

        dialog.show()
    }

    private fun animateBreathingMessage(tv: TextView) {
        val scaleX = android.animation.ObjectAnimator.ofFloat(tv, "scaleX", 1f, 1.05f, 1f)
        val scaleY = android.animation.ObjectAnimator.ofFloat(tv, "scaleY", 1f, 1.05f, 1f)

        scaleX.duration = 1200
        scaleX.repeatCount = ValueAnimator.INFINITE
        scaleX.repeatMode = ValueAnimator.RESTART

        scaleY.duration = 1200
        scaleY.repeatCount = ValueAnimator.INFINITE
        scaleY.repeatMode = ValueAnimator.RESTART

        val animatorSet = android.animation.AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.start()
    }





    private fun showAllCardBacks() {
        row1.removeAllViews()
        row2.removeAllViews()

        val screenWidth = resources.displayMetrics.widthPixels
        val cardWidth = (screenWidth / 3.5).toInt()
        val cardHeight = (cardWidth * 1.5).toInt()

        // First row - 3 cards
        for (i in 0 until 3) {
            val cardBackView = createCardBackView(i, cardWidth, cardHeight)
            row1.addView(cardBackView)
            animateDealing(cardBackView, i) // add animation
        }

        // Second row - 2 cards
        for (i in 3 until 5) {
            val cardBackView = createCardBackView(i, cardWidth, cardHeight)
            row2.addView(cardBackView)
            animateDealing(cardBackView, i) // add animation
        }
    }
    private fun animateDealing(view: View, index: Int) {
        view.alpha = 0f
        view.translationY = -200f // card starts a bit above, like from deck

        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay((index * 300).toLong()) // stagger delay (0.3s each card)
            .setDuration(500)
            .start()
    }



    private fun createCardBackView(index: Int, width: Int, height: Int): View {
        val cardBackView = layoutInflater.inflate(R.layout.item_card_back, null)

        val layoutParams = LinearLayout.LayoutParams(width, height).apply {
            setMargins(12, 0, 12, 0)
        }

        cardBackView.layoutParams = layoutParams
        cardBackView.tag = index

        cardBackView.setOnClickListener {
            val cardIndex = it.tag as Int
            if (!revealedCards.contains(cardIndex)) {
                revealCard(cardIndex)
            }
        }

        return cardBackView
    }


    private fun revealCard(index: Int) {
        revealedCards.add(index)

        // Determine which row and position this card is in
        val row: LinearLayout
        val positionInRow: Int

        if (index < 3) {
            // Card is in first row (positions 0, 1, 2)
            row = row1
            positionInRow = index
        } else {
            // Card is in second row (positions 3, 4)
            row = row2
            positionInRow = index - 3
        }

        // Get the card back view at this position
        val cardBackView = row.getChildAt(positionInRow)

        // Create flip animations
        val flipOut = ScaleAnimation(
            1f, 0f, // X scale from 1 to 0
            1f, 1f, // Y scale remains 1
            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot X at center
            Animation.RELATIVE_TO_SELF, 0.5f  // Pivot Y at center
        ).apply {
            duration = 400
            fillAfter = true
        }

        val flipIn = ScaleAnimation(
            0f, 1f, // X scale from 0 to 1
            1f, 1f, // Y scale remains 1
            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot X at center
            Animation.RELATIVE_TO_SELF, 0.5f  // Pivot Y at center
        ).apply {
            duration = 400
            fillAfter = true
        }

        flipOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                // Replace card back with actual card
                val cardView = createCardView(randomCards[index], index)

                // Copy layout params from card back
                cardView.layoutParams = cardBackView.layoutParams

                // Replace the view
                row.removeViewAt(positionInRow)
                row.addView(cardView, positionInRow)

                cardView.startAnimation(flipIn)

                // Check if all cards are revealed
                if (revealedCards.size == 5) {
                    instructionText.text = "All cards revealed!"
                    btnInterpret.isVisible = true
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        cardBackView.startAnimation(flipOut)
    }

    private fun createCardView(card: TarotMeaning, index: Int): View {
        val cardLayout = layoutInflater.inflate(R.layout.item_card_large, null)

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
            } else {
                // Fallback if image not found
                cardImage.setImageResource(android.R.drawable.ic_dialog_info)
            }
        }

        // Click listener to show details
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


    private fun startReadingAnimation() {
        btnInterpret.isEnabled = false


        val progressBar = findViewById<ProgressBar>(R.id.loadingProgressCircle)
        val percentText = findViewById<TextView>(R.id.loadingPercent)

        loadingLayout.isVisible = true
        loadingText.isVisible = true
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
                    }, 500) // short pause to show 100%
                }
            }
        }
        handler.post(runnable)
    }




    private fun showReadingSummary() {
        val summary = generateReadingSummary(randomCards)
        val loadingtext = findViewById<TextView>(R.id.loadingText)
        loadingtext.isVisible = false
        summaryText.text = summary
        summaryText.isVisible = true
        btnInterpret.isVisible = false
        instructionText.isVisible = false
    }

    private fun generateReadingSummary(cards: List<TarotMeaning>): String {
        val summary = StringBuilder()

        summary.append("🔮 Your 5-Card Tarot Reading Interpretation:\n\n")
        summary.append("Here's the story your cards reveal:\n\n")

        cards.forEachIndexed { index, card ->
            // Use the card data directly (no repository lookup needed)
            val snippet = when {
                card.advice.isNotBlank() -> {
                    card.advice.substringBefore(".").take(100)
                }
                card.meaning_upright.isNotBlank() -> {
                    val shortMeaning = card.meaning_upright.substringBefore(".").take(80)
                    if (card.keywords_upright.isNotEmpty()) {
                        val keywords = card.keywords_upright.take(2).joinToString(" & ")
                        "$keywords - $shortMeaning"
                    } else {
                        shortMeaning
                    }
                }
                card.keywords_upright.isNotEmpty() -> {
                    card.keywords_upright.joinToString(", ")
                }
                card.meaning_reversed.isNotBlank() -> {
                    card.meaning_reversed.substringBefore(".").take(80)
                }
                card.keywords_reversed.isNotEmpty() -> {
                    card.keywords_reversed.joinToString(", ")
                }
                else -> {
                    // Fallback based on card type
                    when (card.arcana) {
                        "Major Arcana" -> "represents a major life lesson or archetypal energy"
                        else -> "indicates developments in the area of ${card.suit?.lowercase() ?: "life"}"
                    }
                }
            }

            val positionText = when (index) {
                0 -> "The first card, **${card.name}**, sets the stage and"
                1 -> "The second card, **${card.name}**, builds upon this and"
                2 -> "At the center, **${card.name}**, reveals the core theme and"
                3 -> "The fourth card, **${card.name}**, develops the story further and"
                4 -> "Finally, **${card.name}**, offers resolution and"
                else -> "Additionally, **${card.name}**, contributes and"
            }

            summary.append("• $positionText $snippet.\n")
        }

        // Add insights about the combination
        summary.append("\n🌟 **Key Insights:**\n")

        // Count Major vs Minor arcana
        val majorCount = cards.count { it.arcana == "Major Arcana" }
        val minorCount = cards.count { it.arcana == "Minor Arcana" }

        if (majorCount >= 3) {
            summary.append("• With $majorCount Major Arcana cards, this reading highlights significant life events and profound transformations.\n")
        }

        // Analyze suits if there are Minor Arcana cards
        if (minorCount > 0) {
            val suits = cards.filter { it.suit != null }.groupBy { it.suit }
            val dominantSuit = suits.maxByOrNull { it.value.size }?.key

            dominantSuit?.let { suit ->
                val suitMeaning = when (suit) {
                    "Cups" -> "emotional matters, relationships, and intuition"
                    "Swords" -> "mental challenges, communication, and decision-making"
                    "Wands" -> "creativity, passion, and new beginnings"
                    "Pentacles" -> "practical affairs, finances, and material security"
                    else -> "various life areas"
                }
                summary.append("• The presence of ${suits[suit]!!.size} $suit cards emphasizes $suitMeaning.\n")
            }
        }

        // Add a personalized conclusion for 5-card spread
        summary.append("\n💫 **Overall Message:**\n")
        summary.append("This comprehensive 5-card spread offers a complete picture of your situation. ")
        summary.append("Each card contributes to a layered narrative that speaks to different aspects of your journey. ")
        summary.append("Take time to reflect on how these energies interact and what guidance they offer for your path forward.\n\n")

        summary.append("✨ Remember: Tarot offers guidance, not fixed destiny. Trust your intuition as you interpret these messages.")

        return summary.toString()
    }

    private fun getAllTarotMeanings(): Map<String, Triple<String, String, String>> {
        // Simplified for testing - add all 78 cards later
        return mapOf(
            // MAJOR ARCANA
            "The Fool" to Triple(
                "New beginnings, innocence, spontaneity, free spirit",
                "Innocence, new beginnings, free spirit, adventure",
                "Recklessness, taken advantage of, inconsideration, bad decisions"
            ),
            "The Magician" to Triple(
                "Willpower, desire, creation, manifestation, resourcefulness",
                "Manifestation, resourcefulness, power, inspired action",
                "Manipulation, untrustworthy, unused potential, trickery"
            ),
            "The High Priestess" to Triple(
                "Intuition, unconscious knowledge, divine feminine, inner voice",
                "Intuitive, unconscious awareness, inner voice, insight",
                "Secrets, disconnected from intuition, withdrawal, silence"
            ),
            "The Empress" to Triple(
                "Femininity, beauty, nature, nurturing, abundance",
                "Femininity, beauty, nature, nurturing, abundance",
                "Creative block, dependence on others, smothering, insecurity"
            ),
            "The Emperor" to Triple(
                "Authority, structure, control, fatherhood, leadership",
                "Authority, establishment, structure, control, fatherhood",
                "Domination, excessive control, rigidity, inflexibility"
            ),
            "The Hierophant" to Triple(
                "Spiritual wisdom, tradition, conformity, morality, ethics",
                "Conformity, tradition, morality, ethics, spiritual guidance",
                "Rebellion, subversiveness, new approaches, ignorance"
            ),
            "The Lovers" to Triple(
                "Love, harmony, relationships, values, choices, alignment",
                "Love, harmony, relationships, values, choices, alignment",
                "Disharmony, imbalance, misalignment of values, bad choices"
            ),
            "The Chariot" to Triple(
                "Control, willpower, success, action, determination",
                "Control, willpower, success, action, determination, victory",
                "Lack of control, aggression, no direction, no direction"
            ),
            "Strength" to Triple(
                "Courage, persuasion, influence, compassion, inner strength",
                "Courage, bravery, compassion, influence, inner strength",
                "Inner weakness, insecurity, low energy, raw emotion"
            ),
            "The Hermit" to Triple(
                "Soul-searching, introspection, guidance, solitude, withdrawal",
                "Soul-searching, introspection, guidance, solitude, withdrawal",
                "Isolation, withdrawal, loneliness, rejection, misguidance"
            ),
            "Wheel of Fortune" to Triple(
                "Good luck, karma, life cycles, destiny, turning point",
                "Good luck, karma, life cycles, destiny, a turning point",
                "Bad luck, resistance to change, breaking cycles, setbacks"
            ),
            "Justice" to Triple(
                "Fairness, truth, cause and effect, law, clarity",
                "Justice, fairness, truth, cause and effect, law",
                "Unfairness, dishonesty, lack of accountability, wrong decisions"
            ),
            "The Hanged Man" to Triple(
                "Surrender, new perspective, enlightenment, letting go",
                "Surrender, new perspective, enlightenment, letting go, pause",
                "Stalling, needless sacrifice, fear of sacrifice, stalling"
            ),
            "Death" to Triple(
                "End of cycle, transitions, letting go, release, metamorphosis",
                "Endings, change, transformation, transition, letting go",
                "Resistance to change, inability to move on, fear of change"
            ),
            "Temperance" to Triple(
                "Balance, moderation, patience, purpose, meaning, blending",
                "Balance, moderation, patience, purpose, meaning, combination",
                "Imbalance, excess, lack of long-term vision, recklessness"
            ),
            "The Devil" to Triple(
                "Shadow self, attachment, addiction, restriction, sexuality",
                "Shadow self, attachment, addiction, restriction, sexuality",
                "Releasing limiting beliefs, exploring dark thoughts, detachment"
            ),
            "The Tower" to Triple(
                "Sudden change, upheaval, chaos, revelation, awakening",
                "Sudden change, upheaval, chaos, revelation, awakening",
                "Fear of change, disaster avoidance, delaying the inevitable"
            ),
            "The Star" to Triple(
                "Hope, faith, purpose, renewal, spirituality, inspiration",
                "Hope, faith, purpose, renewal, spirituality, inspiration",
                "Lack of faith, despair, self-trust issues, disconnection"
            ),
            "The Moon" to Triple(
                "Illusion, fear, anxiety, subconscious, intuition, uncertainty",
                "Illusion, fear, anxiety, subconscious, intuition, uncertainty",
                "Release of fear, repressed emotion, inner confusion, misinterpretation"
            ),
            "The Sun" to Triple(
                "Positivity, fun, warmth, success, vitality, joy",
                "Positivity, fun, warmth, success, vitality, joy, enlightenment",
                "Temporary depression, lack of success, unrealistic expectations, sadness"
            ),
            "Judgement" to Triple(
                "Reflection, reckoning, awakening, absolution, inner calling",
                "Reflection, reckoning, awakening, absolution, inner calling",
                "Self-doubt, refusal of self-examination, fear of judgment"
            ),
            "The World" to Triple(
                "Completion, achievement, fulfillment, harmony, travel, wholeness",
                "Completion, achievement, fulfillment, harmony, travel, wholeness",
                "Incompletion, lack of closure, lack of achievement, stagnation"

            ),
            // CUPS
            "Ace of Cups" to Triple(
                "New feelings, spirituality, intuition, love, compassion",
                "New feelings, spirituality, intuition, love, compassion, creativity",
                "Blocked creativity, emotional loss, emptiness, feeling unloved"
            ),
            "Two of Cups" to Triple(
                "Unity, partnership, connection, attraction, relationships",
                "Unity, partnership, connection, attraction, relationships, mutual respect",
                "Breakup, imbalance, miscommunication, tension, separation"
            ),
            "Three of Cups" to Triple(
                "Friendship, community, gatherings, celebrations, collaborations",
                "Friendship, community, gatherings, celebrations, collaborations, joy",
                "Gossip, isolation, exclusion, conflict, overindulgence"
            ),
            "Four of Cups" to Triple(
                "Apathy, contemplation, disconnection, melancholy, boredom",
                "Apathy, contemplation, disconnection, melancholy, reevaluation",
                "New opportunities, reconnection, depression, withdrawal, missed chances"
            ),
            "Five of Cups" to Triple(
                "Loss, grief, disappointment, regret, misfortune",
                "Loss, grief, disappointment, regret, focusing on the negative",
                "Moving on, acceptance, finding peace, recovery, personal setbacks"
            ),
            "Six of Cups" to Triple(
                "Familiarity, happy memories, healing, comfort, nostalgia",
                "Familiarity, happy memories, healing, comfort, nostalgia, reunion",
                "Living in the past, stagnation, being held back, moving forward"
            ),
            "Seven of Cups" to Triple(
                "Choices, wishful thinking, illusion, fantasy, overwhelm",
                "Choices, wishful thinking, illusion, fantasy, many options",
                "Lack of purpose, disarray, confusion, escapism, bad decisions"
            ),
            "Eight of Cups" to Triple(
                "Walking away, disillusionment, leaving behind, search for truth",
                "Walking away, disillusionment, leaving behind, search for meaning",
                "Stagnation, fear of change, avoidance, staying in bad situation"
            ),
            "Nine of Cups" to Triple(
                "Satisfaction, emotional stability, luxury, contentment, wishes fulfilled",
                "Satisfaction, emotional stability, luxury, contentment, wishes fulfilled",
                "Smugness, dissatisfaction, inner happiness at risk, overindulgence"
            ),
            "Ten of Cups" to Triple(
                "Divine love, blissful relationships, harmony, alignment, happy family",
                "Divine love, blissful relationships, harmony, alignment, happy family",
                "Disconnection, misalignment, family problems, domestic chaos"
            ),
            "Page of Cups" to Triple(
                "Creative opportunities, intuitive messages, curiosity, new feelings",
                "Creative opportunities, intuitive messages, curiosity, new feelings",
                "Creative block, emotional immaturity, insecurity, disappointment"
            ),
            "Knight of Cups" to Triple(
                "Romance, charm, imagination, beauty, following the heart",
                "Romance, charm, imagination, beauty, following the heart, idealist",
                "Unrealistic, jealousy, moodiness, disappointment, emotional manipulation"
            ),
            "Queen of Cups" to Triple(
                "Compassion, calm, comfort, emotional security, intuition",
                "Compassion, calm, comfort, emotional security, intuition, warmth",
                "Inner feelings, insecurity, dependence, emotional manipulation"
            ),
            "King of Cups" to Triple(
                "Emotional balance, compassion, diplomacy, control, generosity",
                "Emotional balance, compassion, diplomacy, control, generosity",
                "Emotional manipulation, moodiness, emotional abuse, coldness"

            ),
            // SWORDS
            "Ace of Swords" to Triple(
                "Breakthrough, new ideas, mental clarity, success, truth",
                "Breakthrough, new ideas, mental clarity, success, truth, justice",
                "Confusion, chaos, lack of clarity, disruption, mental blocks"
            ),
            "Two of Swords" to Triple(
                "Difficult choices, indecision, stalemate, blockage, truce",
                "Difficult choices, indecision, stalemate, blockage, avoidance",
                "Indecision, confusion, information overload, no right choice"
            ),
            "Three of Swords" to Triple(
                "Heartbreak, emotional pain, sorrow, grief, rejection",
                "Heartbreak, emotional pain, sorrow, grief, rejection, separation",
                "Recovery, forgiveness, moving on, reconciliation, healing"
            ),
            "Four of Swords" to Triple(
                "Rest, restoration, contemplation, recuperation, meditation",
                "Rest, restoration, contemplation, recuperation, meditation, peace",
                "Restlessness, burnout, stress, exhaustion, resistance to rest"
            ),
            "Five of Swords" to Triple(
                "Conflict, tension, loss, defeat, win at all costs, betrayal",
                "Conflict, tension, loss, defeat, win at all costs, unbridled ambition",
                "Reconciliation, making amends, past resentment, desire to reconcile"
            ),
            "Six of Swords" to Triple(
                "Transition, change, rite of passage, releasing baggage, moving on",
                "Transition, change, rite of passage, releasing baggage, moving forward",
                "Emotional baggage, unresolved issues, resistance to transition"
            ),
            "Seven of Swords" to Triple(
                "Deception, trickery, tactics, strategy, avoidance, betrayal",
                "Deception, trickery, tactics, strategy, avoidance, getting away",
                "Coming clean, rethinking approach, deception revealed, conscience"
            ),
            "Eight of Swords" to Triple(
                "Restriction, confusion, powerlessness, imprisonment, self-victimization",
                "Restriction, confusion, powerlessness, imprisonment, self-imposed",
                "Self-acceptance, new perspective, freedom, release, overcoming fear"
            ),
            "Nine of Swords" to Triple(
                "Anxiety, worry, fear, depression, nightmare, overwhelming thoughts",
                "Anxiety, worry, fear, depression, nightmare, overwhelming thoughts",
                "Hope, recovery, despair, mental torment, overcoming inner demons"
            ),
            "Ten of Swords" to Triple(
                "Painful endings, deep wounds, betrayal, loss, crisis, back-stabbing",
                "Painful endings, deep wounds, betrayal, loss, crisis, rock bottom",
                "Recovery, regeneration, resisting an end, inevitable end, despair"
            ),
            "Page of Swords" to Triple(
                "Curiosity, restlessness, mental energy, new ideas, thirst for knowledge",
                "Curiosity, restlessness, mental energy, new ideas, thirst for knowledge",
                "Deception, manipulation, all talk, haste, rudeness, immature ideas"
            ),
            "Knight of Swords" to Triple(
                "Action, impulsiveness, defending beliefs, aggression, haste",
                "Action, impulsiveness, defending beliefs, aggression, directness",
                "No direction, disregard for consequences, unpredictability, forcefulness"
            ),
            "Queen of Swords" to Triple(
                "Independent, unbiased judgment, clear boundaries, direct communication",
                "Independent, unbiased judgment, clear boundaries, direct communication",
                "Cold hearted, cruel, bitterness, harsh judgment, emotional vulnerability"
            ),
            "King of Swords" to Triple(
                "Mental clarity, intellectual power, authority, truth, command",
                "Mental clarity, intellectual power, authority, truth, command, integrity",
                "Manipulative, cruel, weakness, tyranny, abuse of power"

            ),
            // WANDS
            "Ace of Wands" to Triple(
                "Inspiration, new opportunities, growth, potential, adventure",
                "Inspiration, new opportunities, growth, potential, adventure, creation",
                "Delays, lack of motivation, weighed down, missed opportunities"
            ),
            "Two of Wands" to Triple(
                "Future planning, progress, decisions, discovery, world at your feet",
                "Future planning, progress, decisions, discovery, world at your feet",
                "Fear of unknown, lack of planning, bad decisions, playing it safe"
            ),
            "Three of Wands" to Triple(
                "Preparation, foresight, enterprise, expansion, foresight",
                "Preparation, foresight, enterprise, expansion, looking ahead",
                "Delays, obstacles, frustration, lack of foresight, disappointment"
            ),
            "Four of Wands" to Triple(
                "Celebration, harmony, marriage, home, community, reunion",
                "Celebration, harmony, marriage, home, community, reunion, stability",
                "Lack of support, transition, home conflict, instability, disharmony"
            ),
            "Five of Wands" to Triple(
                "Disagreement, competition, strife, tension, conflict, rivalry",
                "Disagreement, competition, strife, tension, conflict, differences",
                "Avoiding conflict, respect differences, inner conflict, tension release"
            ),
            "Six of Wands" to Triple(
                "Victory, success, public recognition, progress, self-confidence",
                "Victory, success, public recognition, progress, self-confidence, pride",
                "Egotism, fall from grace, lack of recognition, punishment, vanity"
            ),
            "Seven of Wands" to Triple(
                "Perseverance, defensive, maintaining control, assertiveness, challenge",
                "Perseverance, defensive, maintaining control, assertiveness, protection",
                "Give up, overwhelmed, defensive, yielding, burnout, exhaustion"
            ),
            "Eight of Wands" to Triple(
                "Rapid action, movement, quick decisions, air travel, excitement",
                "Rapid action, movement, quick decisions, air travel, excitement, progress",
                "Delays, frustration, resisting change, panic, waiting, slowdown"
            ),
            "Nine of Wands" to Triple(
                "Resilience, grit, last stand, persistence, test of faith, boundaries",
                "Resilience, grit, last stand, persistence, test of faith, boundaries",
                "Stubbornness, defensiveness, yielding, giving up, struggle, exhaustion"
            ),
            "Ten of Wands" to Triple(
                "Accomplishment, responsibility, burden, extra responsibility, hard work",
                "Accomplishment, responsibility, burden, extra responsibility, completion",
                "Inability to delegate, overstressed, burden, breakdown, oppression"
            ),
            "Page of Wands" to Triple(
                "Exploration, excitement, freedom, optimism, news, inspiration",
                "Exploration, excitement, freedom, optimism, news, inspiration, energy",
                "Bad news, delays, restlessness, lack of direction, pessimism"
            ),
            "Knight of Wands" to Triple(
                "Action, adventure, fearlessness, energy, passion, free spirit",
                "Action, adventure, fearlessness, energy, passion, free spirit, impulsiveness",
                "Haste, scattered energy, delays, frustration, lack of direction, recklessness"
            ),
            "Queen of Wands" to Triple(
                "Courage, confidence, independence, determination, joy, warmth",
                "Courage, confidence, independence, determination, joy, warmth, charisma",
                "Self-doubt, lack of self-confidence, jealousy, insecurity, selfishness"
            ),
            "King of Wands" to Triple(
                "Leadership, vision, entrepreneurship, honor, charm, boldness",
                "Leadership, vision, entrepreneurship, honor, charm, boldness, inspiration",
                "Impulsiveness, arrogance, overbearing, lack of direction, rash decisions"
            ),
            // PENTACLES
            "Ace of Pentacles" to Triple(
                "New financial opportunities, prosperity, new ventures, manifestation",
                "New financial opportunities, prosperity, new ventures, manifestation, stability",
                "Missed opportunities, lack of planning, financial setbacks, greed"
            ),
            "Two of Pentacles" to Triple(
                "Balance, adaptability, time management, juggling priorities, flexibility",
                "Balance, adaptability, time management, juggling priorities, multitasking",
                "Overwhelm, disorganization, lack of balance, financial stress"
            ),
            "Three of Pentacles" to Triple(
                "Teamwork, collaboration, skill development, craftsmanship, recognition",
                "Teamwork, collaboration, skill development, craftsmanship, recognition",
                "Lack of teamwork, poor collaboration, lack of recognition, disorganization"
            ),
            "Four of Pentacles" to Triple(
                "Control, stability, security, possession, conservation, greed",
                "Control, stability, security, possession, conservation, materialism",
                "Greed, materialism, insecurity, loss of control, financial instability"
            ),
            "Five of Pentacles" to Triple(
                "Financial loss, poverty, isolation, insecurity, worry",
                "Financial loss, poverty, isolation, insecurity, worry, hardship",
                "Recovery, improvement, financial stability, overcoming adversity"
            ),
            "Six of Pentacles" to Triple(
                "Generosity, charity, giving and receiving, balance, fairness",
                "Generosity, charity, giving and receiving, balance, fairness, support",
                "Selfishness, inequality, imbalance, financial irresponsibility"
            ),
            "Seven of Pentacles" to Triple(
                "Long-term view, perseverance, investment, growth, assessment",
                "Long-term view, perseverance, investment, growth, assessment, patience",
                "Lack of progress, impatience, poor planning, wasted effort"
            ),
            "Eight of Pentacles" to Triple(
                "Skill development, craftsmanship, dedication, mastery, hard work",
                "Skill development, craftsmanship, dedication, mastery, hard work, diligence",
                "Lack of focus, poor workmanship, lack of ambition, laziness"
            ),
            "Nine of Pentacles" to Triple(
                "Abundance, luxury, self-sufficiency, financial independence, prosperity",
                "Abundance, luxury, self-sufficiency, financial independence, prosperity",
                "Financial setbacks, lack of resources, dependence on others"
            ),
            "Ten of Pentacles" to Triple(
                "Wealth, inheritance, family, long-term success, stability",
                "Wealth, inheritance, family, long-term success, stability, legacy",
                "Financial failure, lack of stability, family disputes, loss of inheritance"
            ),
            "Page of Pentacles" to Triple(
                "Manifestation, financial opportunity, ambition, desire, new beginnings",
                "Manifestation, financial opportunity, ambition, desire, new beginnings, practicality",
                "Lack of progress, procrastination, missed opportunities, lack of focus"
            ),
            "Knight of Pentacles" to Triple(
                "Hard work, routine, responsibility, practicality, reliability",
                "Hard work, routine, responsibility, practicality, reliability, patience",
                "Stagnation, laziness, lack of progress, over-cautiousness"
            ),
            "Queen of Pentacles" to Triple(
                "Nurturing, practicality, financial security, comfort, generosity",
                "Nurturing, practicality, financial security, comfort, generosity, abundance",
                "Financial insecurity, lack of self-care, neglecting responsibilities"
            ),
            "King of Pentacles" to Triple(
                "Wealth, business, leadership, security, discipline, abundance",
                "Wealth, business, leadership, security, discipline, abundance, success",
                "Financial irresponsibility, lack of discipline, greed, materialism"
            )
        )
    }
}