package io.evlikat.games.cardgames.core

class Deck private constructor(private val cards: MutableList<Card>) : CardZone {

    override lateinit var watcher: Watcher

    override val size: Int get() = cards.size

    companion object {
        fun standard52(): Deck = Deck(Card.values().toMutableList())
    }

    fun shuffle() = cards.shuffle()

    override fun draw(): Card {
        return cards.removeAt(cards.lastIndex)
    }

    override fun draw(card: Card): Card {
        if (!cards.remove(card)) {
            throw IllegalStateException("The $card is not in hand")
        }
        return card
    }

    override fun putOnTop(card: Card) {
        cards.add(card)
    }
}