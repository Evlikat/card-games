package io.evlikat.games.cardgames.ginrummy.server.player

import io.evlikat.games.cardgames.core.Card
import io.evlikat.games.cardgames.core.CardZones
import io.evlikat.games.cardgames.core.Player
import io.evlikat.games.cardgames.ginrummy.server.AskSelectCard
import io.evlikat.games.cardgames.ginrummy.server.AskSelectZone
import io.evlikat.games.cardgames.ginrummy.server.AskYesNo
import kotlinx.coroutines.CompletableDeferred
import org.slf4j.LoggerFactory

class RemoteWsPlayer(
    override val name: String,
    val clientId: String,
    val messageSender: MessageSender
) : Player {

    private val log = LoggerFactory.getLogger(RemoteWsPlayer::class.java)

    private var yesNoDeferred: CompletableDeferred<Boolean>? = null

    private var selectZoneDeferred: CompletableDeferred<CardZones>? = null

    private var selectCardDeferred: CompletableDeferred<Card>? = null

    fun resolveYesNo(yes: Boolean) {
        yesNoDeferred?.complete(yes)
    }

    override suspend fun askYesNo(message: String): Boolean {
        log.debug("Awaiting {} to select yes/no", clientId)
        val newYesNoDeferred = CompletableDeferred<Boolean>()
        yesNoDeferred = newYesNoDeferred
        messageSender.send(AskYesNo(message))
        return newYesNoDeferred.await()
    }

    fun resolveSelectZone(zone: CardZones) {
        selectZoneDeferred?.complete(zone)
    }

    override suspend fun askSelectZone(message: String, vararg cardZones: CardZones): CardZones {
        log.debug("Awaiting {} to select a zone", clientId)
        val newSelectZoneDeferred = CompletableDeferred<CardZones>()
        selectZoneDeferred = newSelectZoneDeferred
        messageSender.send(AskSelectZone(message, cardZones.toList()))
        return newSelectZoneDeferred.await()
    }

    fun resolveSelectCard(card: Card) {
        selectCardDeferred?.complete(card)
    }

    override suspend fun askSelectCard(message: String, cards: Collection<Card>): Card {
        log.debug("Awaiting {} to select a card", clientId)
        val newSelectCardDeferred = CompletableDeferred<Card>()
        selectCardDeferred = newSelectCardDeferred
        messageSender.send(AskSelectCard(message, cards))
        return newSelectCardDeferred.await()
    }
}