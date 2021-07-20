package io.evlikat.games.cardgames.ginrummy

import io.evlikat.games.cardgames.core.Card
import io.evlikat.games.cardgames.core.CardSet

data class GRun(private val cards: CardSet) : CardSet by cards {
    override fun minus(card: Card): GRun = GRun(cards - card)

    fun isValid(): Boolean =
        (cards.last().nominal.ordinal - cards.first().nominal.ordinal == cards.size + 1) && cards.size >= 3
}

data class GSet(private val cards: CardSet) : CardSet by cards {
    override fun minus(card: Card): GSet = GSet(cards - card)

    fun isValid(): Boolean = cards.size >= 3
}