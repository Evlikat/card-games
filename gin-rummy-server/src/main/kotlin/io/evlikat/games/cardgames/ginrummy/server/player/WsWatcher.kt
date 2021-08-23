package io.evlikat.games.cardgames.ginrummy.server.player

import io.evlikat.games.cardgames.core.Card
import io.evlikat.games.cardgames.core.CardZone
import io.evlikat.games.cardgames.core.CardZones
import io.evlikat.games.cardgames.core.Watcher
import io.evlikat.games.cardgames.ginrummy.server.CardMoved

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
}