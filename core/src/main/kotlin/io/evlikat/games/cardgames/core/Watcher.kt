package io.evlikat.games.cardgames.core

interface Watcher {
    fun cardMoved(card: Card, from: CardZone, to: CardZone)

    fun gameOver(result: GameResult)
}