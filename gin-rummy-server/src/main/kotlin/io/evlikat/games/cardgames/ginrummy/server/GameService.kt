package io.evlikat.games.cardgames.ginrummy.server

import io.evlikat.games.cardgames.core.Card
import io.evlikat.games.cardgames.core.CardZones
import io.evlikat.games.cardgames.ginrummy.GinRummyGame
import io.evlikat.games.cardgames.ginrummy.server.player.RemoteWsPlayer
import io.evlikat.games.cardgames.ginrummy.server.player.WsWatcher
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@Service
class GameService(
    private val messagingTemplate: SimpMessagingTemplate
) {

    private val log = LoggerFactory.getLogger(GameService::class.java)

    private val games = ConcurrentHashMap<String, GameArgs>()
    private val gamePool = Executors.newFixedThreadPool(1)

    private val gameIdCounter = AtomicInteger(1)

    fun createNewGame(creation: GameCreation): GameCreated {
        val game = GinRummyGame()
        val gameId = gameIdCounter.getAndIncrement().toString() //TODO: UUID.randomUUID().toString()
        games[gameId] = GameArgs(
            player1 = RemoteWsPlayer(
                name = creation.playerName,
                clientId = creation.clientId,
                messageSender = SimpMessageSender(messagingTemplate, gameId, creation.clientId)
            ),
            game = game
        )

        log.info("Game $gameId created by ${creation.playerName}")

        return GameCreated(gameId = gameId)
    }

    fun joinGame(joining: GameJoining): GameJoined {
        val gameArgs = games[joining.gameId] ?: throw IllegalArgumentException("Game not found")
        val game = gameArgs.game

        log.info("${joining.playerName} has joined to ${joining.gameId}")

        val currentPlayer2 = gameArgs.player2
        val player2 = if (currentPlayer2 != null) {
            currentPlayer2
        } else {
            val newPlayer2 = RemoteWsPlayer(
                name = joining.playerName,
                clientId = joining.clientId,
                messageSender = SimpMessageSender(messagingTemplate, joining.gameId, joining.clientId)
            )
            gameArgs.player2 = newPlayer2
            newPlayer2
        }

        gamePool.submit {
            game.go(
                player1 = gameArgs.player1,
                player2 = player2,
                watcher = WsWatcher(gameArgs.player1.messageSender, player2.messageSender)
            )
        }

        return GameJoined(joining.gameId)
    }

    fun play(gameId: String, baseTellMessage: BaseTellMessage) {
        val gameArgs = games[gameId] ?: throw IllegalArgumentException("Game not found")

        when (baseTellMessage.actor) {
            gameArgs.player1.clientId -> deliver(gameArgs.player1, baseTellMessage)
            gameArgs.player2?.clientId -> deliver(gameArgs.player2!!, baseTellMessage)
            else -> throw IllegalArgumentException("Player not found")
        }
    }

    private fun deliver(player: RemoteWsPlayer, message: BaseTellMessage) {
        return when (message) {
            is TellYesNo -> player.resolveYesNo(message.yes)
            is TellSelectCard -> player.resolveSelectCard(Card.valueOf(message.card))
            is TellSelectZone -> player.resolveSelectZone(CardZones.valueOf(message.zone))
        }
    }
}

data class GameArgs(
    val player1: RemoteWsPlayer,
    val game: GinRummyGame,
) {
    var player2: RemoteWsPlayer? = null
}

data class GameCreation(
    val playerName: String,
    val clientId: String
)

data class GameJoining(
    val gameId: String,
    val playerName: String,
    val clientId: String
)

data class GameCreated(
    val gameId: String
)

data class GameJoined(
    val gameId: String
)