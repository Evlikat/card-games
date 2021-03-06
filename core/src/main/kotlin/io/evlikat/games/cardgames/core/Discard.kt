package io.evlikat.games.cardgames.core

class Discard(private val cards: MutableList<Card> = mutableListOf()) : CardZone {

    override val cardZones: CardZones = CardZones.DISCARD

    override lateinit var watcher: Watcher

    override val size: Int get() = cards.size

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