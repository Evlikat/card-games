package io.evlikat.games.cardgames.ginrummy

import io.evlikat.games.cardgames.core.Player
import io.evlikat.games.cardgames.core.Watcher

class GinRummyGame {

    fun go(player1: Player, player2: Player, watcher: Watcher): GinRummyGameResult {
        val play = GinRummy(watcher, player1, player2)

        play.hand()
        play.atStartOfGame()
        return play.play()
    }
}