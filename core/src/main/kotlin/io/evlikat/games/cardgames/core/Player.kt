package io.evlikat.games.cardgames.core

interface Player {

    val name: String

    fun askYesNo(message: String): Boolean

    fun askSelectZone(message: String, vararg cardZones: CardZones): CardZones

    fun askSelectCard(message: String, cards: Collection<Card>): Card
}