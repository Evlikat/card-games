package io.evlikat.games.cardgames.ginrummy.server.player

import io.evlikat.games.cardgames.ginrummy.server.BaseServerMessage

interface MessageSender {

    fun send(message: BaseServerMessage)
}