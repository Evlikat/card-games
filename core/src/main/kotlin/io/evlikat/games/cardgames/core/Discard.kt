package io.evlikat.games.cardgames.core

class Discard(private val cards: MutableList<Card> = mutableListOf()) : CardZone {

    override lateinit var watcher: Watcher

    val allCards: List<Card> = cards

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