package io.evlikat.games.cardgames.core

class Player(
    val name: String
) {
    fun askYesNo(message: String): Boolean {
        TODO("Not yet implemented")
    }

    fun askSelectZone(message: String, vararg cardZones: CardZone): CardZone {
        TODO("Not yet implemented")
    }

    fun askSelectCard(message: String, cards: Collection<Card>): Card {
        TODO("Not yet implemented")
    }
}