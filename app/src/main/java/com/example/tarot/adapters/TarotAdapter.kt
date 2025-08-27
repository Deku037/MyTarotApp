package com.example.tarot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.tarot.model.TarotMeaning
import kotlin.random.Random

data class CardWithOrientation(
    val card: TarotMeaning,
    val isUpright: Boolean
)

class TarotAdapter(
    private val cards: List<CardWithOrientation>,
    private val onCardClick: (CardWithOrientation) -> Unit
) : RecyclerView.Adapter<TarotAdapter.TarotViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TarotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return TarotViewHolder(view)
    }

    override fun onBindViewHolder(holder: TarotViewHolder, position: Int) {
        holder.bind(cards[position], position)
    }

    override fun getItemCount() = cards.size

    inner class TarotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.cardImage)

        fun bind(cardWithOrientation: CardWithOrientation, position: Int) {
            val card = cardWithOrientation.card
            card.img?.let {
                val resId = itemView.context.resources.getIdentifier(it, "drawable", itemView.context.packageName)
                imageView.setImageResource(resId)
            }

            imageView.rotation = if (cardWithOrientation.isUpright) 0f else 180f

            itemView.setOnClickListener {
                onCardClick(cardWithOrientation)
            }

            // Animate card draw
            itemView.translationX = -500f
            itemView.alpha = 0f
            itemView.animate()
                .translationX(0f)
                .alpha(1f)
                .setStartDelay((position * 150).toLong())
                .setDuration(400)
                .start()
        }
    }
}
