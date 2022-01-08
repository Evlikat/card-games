package io.evlikat.games.cardgames.ginrummy.server.player

import io.evlikat.games.cardgames.core.Card
import io.evlikat.games.cardgames.core.CardZone
import io.evlikat.games.cardgames.core.CardZones
import io.evlikat.games.cardgames.core.Player
import io.evlikat.games.cardgames.ginrummy.server.AskSelectCard
import io.evlikat.games.cardgames.ginrummy.server.AskSelectZone
import io.evlikat.games.cardgames.ginrummy.server.AskYesNo
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class RemoteWsPlayer(
    override val name: String,
    val clientId: String,
    val messageSender: MessageSender
) : Player {

    private val lock = ReentrantLock(false)

    private val yesNoCondition = lock.newCondition()

    @Volatile
    private var lastYes: Boolean = false


    private val selectZoneCondition = lock.newCondition()

    @Volatile
    private var lastSelectedZone: CardZones? = null


    private val selectCardCondition = lock.newCondition()

    @Volatile
    private var lastSelectedCard: Card? = null

    fun resolveYesNo(yes: Boolean) {
        lastYes = yes
        lock.withLock {
            yesNoCondition.signal()
        }
    }

    override fun askYesNo(message: String): Boolean {
        messageSender.send(AskYesNo(message))
        lock.withLock {
            yesNoCondition.await()
            return lastYes
        }
    }

    fun resolveSelectZone(zone: CardZones) {
        lastSelectedZone = zone
        lock.withLock {
            selectZoneCondition.signal()
        }
    }

    override fun askSelectZone(message: String, vararg cardZones: CardZones): CardZones {
        messageSender.send(AskSelectZone(message, cardZones.toList()))
        lock.withLock {
            selectZoneCondition.await()
            return lastSelectedZone!!
        }
    }

    fun resolveSelectCard(card: Card) {
        lastSelectedCard = card
        lock.withLock {
            selectCardCondition.signal()
        }
    }

    override fun askSelectCard(message: String, cards: Collection<Card>): Card {
        messageSender.send(AskSelectCard(message, cards))
        lock.withLock {
            selectCardCondition.await()
            return lastSelectedCard!!
        }
    }
}