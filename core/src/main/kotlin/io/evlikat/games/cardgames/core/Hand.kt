package io.evlikat.games.cardgames.core

class Hand(val player: Player, override val cardZones: CardZones) : CardZone {

    override lateinit var watcher: Watcher
    var cards: CardSet = EmptyCardSet
        private set
    override val size: Int get() = cards.size

    override fun draw(): Card {
        TODO("Not drawable zone")
    }

    override fun draw(card: Card): Card {
        if (!cards.contains(card)) {
            throw IllegalStateException("The $card is not in hand")
        }
        cards -= card
        return card
    }

    override fun putOnTop(card: Card) {
        cards += card
    }

}