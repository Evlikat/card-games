package io.evlikat.games.cardgames.ginrummy

import io.evlikat.games.cardgames.core.Player
import io.evlikat.games.cardgames.core.Watcher

class GinRummyGame {

    fun go(player1: Player, player2: Player) {
        val watcher = Watcher()
        val play = GinRummy(watcher, player1, player2)

        play.hand()
        play.atStartOfGame()
        (1..100).forEach { turnNumber ->
            play.turn(turnNumber, player1)
            play.turn(turnNumber, player2)
        }
    }
}