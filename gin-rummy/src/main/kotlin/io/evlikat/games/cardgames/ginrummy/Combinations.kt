package io.evlikat.games.cardgames.ginrummy

import io.evlikat.games.cardgames.core.Card
import io.evlikat.games.cardgames.core.CardSet
import io.evlikat.games.cardgames.core.Nominal

interface Combination {

    fun offer(card: Card): Boolean
}

data class GRun(private val cards: CardSet) : Combination, CardSet by cards {

    private val firstCard: Card
    private val lastCard: Card

    init {
        val sortedCards = cards.sorted()
        firstCard = sortedCards.first()
        lastCard = sortedCards.last()
        if (!isRun(sortedCards)) {
            throw IllegalArgumentException("Run must contain at least 3 cards of the same suit in a row")
        }
    }

    override fun minus(card: Card): CardSet {
        val newCardSet = cards - card
        return if (isRun(newCardSet.sorted())) GRun(newCardSet) else cards - card
    }

    override fun offer(card: Card): Boolean = card precedes firstCard || lastCard precedes card

    companion object {
        private fun isRun(sortedCards: List<Card>): Boolean {
            if (sortedCards.size < 3) {
                return false
            }
            var previous = sortedCards.first()
            for (i in 1 until sortedCards.size) {
                val card = sortedCards[i]
                if (card.suit != previous.suit) {
                    return false
                }
                if (card.nominal.ordinal != previous.nominal.ordinal + 1) {
                    return false
                }
                previous = card
            }
            return true
        }
    }
}

data class GSet(private val cards: CardSet) : Combination, CardSet by cards {

    private val nominal: Nominal

    init {
        val first = cards.first()
        nominal = first.nominal
        if (cards.size < 3 || cards.any { first.nominal != it.nominal }) {
            throw IllegalArgumentException("Set must contain at least 3 cards of the same nominal")
        }
    }

    override fun minus(card: Card): CardSet {
        val newCardSet = cards - card
        return if (isSet(newCardSet)) GSet(newCardSet) else cards - card
    }

    override fun offer(card: Card): Boolean = card !in cards && nominal == card.nominal

    companion object {
        private fun isSet(cards: CardSet): Boolean {
            val first = cards.first()
            return cards.size >= 3 && cards.all { first.nominal == it.nominal }
        }
    }
}