
package com.example.tarot

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tarot.model.TarotCard

class CardDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_detail)

        // Get data from intent
        val cardName = intent.getStringExtra("CARD_NAME") ?: "Unknown Card"
        val cardImage = intent.getStringExtra("CARD_IMAGE") ?: ""
        val cardNumber = intent.getStringExtra("CARD_NUMBER") ?: ""
        val cardArcana = intent.getStringExtra("CARD_ARCANA") ?: ""
        val cardSuit = intent.getStringExtra("CARD_SUIT") ?: ""

        // Initialize views
        val cardDetailImage = findViewById<ImageView>(R.id.cardDetailImage)
        val cardDetailName = findViewById<TextView>(R.id.cardDetailName)
        val cardDetailNumber = findViewById<TextView>(R.id.cardDetailNumber)
        val cardDetailDescription = findViewById<TextView>(R.id.cardDetailDescription)
        val cardDetailUpright = findViewById<TextView>(R.id.cardDetailUpright)
        val cardDetailReversed = findViewById<TextView>(R.id.cardDetailReversed)

        // Set card data
        cardDetailName.text = cardName

        val details = "#$cardNumber • $cardArcana"
        cardDetailNumber.text = if (cardSuit.isNotEmpty() && cardSuit != "null") {
            "$details • $cardSuit"
        } else {
            details
        }

        // Load card image
        if (cardImage.isNotEmpty()) {
            val resourceId = resources.getIdentifier(
                cardImage.replace(".jpg", ""),
                "drawable",
                packageName
            )
            if (resourceId != 0) {
                cardDetailImage.setImageResource(resourceId)
            }
        }

        // Set card meanings for all 78 cards
        setCardMeanings(cardName, cardDetailDescription, cardDetailUpright, cardDetailReversed)
    }

    private fun setCardMeanings(
        cardName: String,
        descriptionView: TextView,
        uprightView: TextView,
        reversedView: TextView
    ) {
        val meanings = getAllTarotMeanings()
        val meaning = meanings[cardName] ?: Triple(
            "General tarot card meaning",
            "Positive aspects of this card",
            "Challenges or reversed meaning"
        )

        descriptionView.text = meaning.first
        uprightView.text = meaning.second
        reversedView.text = meaning.third
    }

    private fun getAllTarotMeanings(): Map<String, Triple<String, String, String>> {
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