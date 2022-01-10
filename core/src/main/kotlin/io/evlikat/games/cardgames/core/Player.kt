package io.evlikat.games.cardgames.core

interface Player {

    val name: String

    suspend fun askYesNo(message: String): Boolean

    suspend fun askSelectZone(message: String, vararg cardZones: CardZones): CardZones

    suspend fun askSelectCard(message: String, cards: Collection<Card>): Card
}