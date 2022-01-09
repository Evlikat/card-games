package io.evlikat.games.cardgames.ginrummy.server.player

import io.evlikat.games.cardgames.core.*
import io.evlikat.games.cardgames.ginrummy.server.CardMoved
import io.evlikat.games.cardgames.ginrummy.server.GameOver

class WsWatcher(
    private val messageSender1: MessageSender,
    private val messageSender2: MessageSender
) : Watcher {

    override fun cardMoved(card: Card, from: CardZone, to: CardZone) {
        val cardForPlayer1 = if (to.cardZones == CardZones.HAND_2) null else card
        val cardForPlayer2 = if (to.cardZones == CardZones.HAND_1) null else card
        messageSender1.send(CardMoved(cardForPlayer1, from.cardZones, to.cardZones))
        messageSender2.send(CardMoved(cardForPlayer2, from.cardZones, to.cardZones))
    }

    override fun gameOver(result: GameResult) {
        messageSender1.send(GameOver(result.scores[0], result.scores[1]))
        messageSender2.send(GameOver(result.scores[1], result.scores[0]))
    }
}