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
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

@Service
class GameService(
    private val messagingTemplate: SimpMessagingTemplate
) {

    private val log = LoggerFactory.getLogger(GameService::class.java)

    private val games = ConcurrentHashMap<String, GameArgs>()
    private val gamePool = Executors.newFixedThreadPool(1)

    fun createNewGame(creation: GameCreation): GameCreated {
        val game = GinRummyGame()
        val gameId = "1"//UUID.randomUUID().toString()
        games[gameId] = GameArgs(
            player1 = RemoteWsPlayer(
                name = creation.playerName,
                messageSender = SimpMessageSender(messagingTemplate, creation.clientId)
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

        gameArgs.player2 = RemoteWsPlayer(
            name = joining.playerName,
            messageSender = SimpMessageSender(messagingTemplate, joining.clientId)
        )

        gamePool.submit {
            game.go(
                player1 = gameArgs.player1,
                player2 = gameArgs.player2,
                watcher = WsWatcher(gameArgs.player1.messageSender, gameArgs.player2.messageSender)
            )
        }

        return GameJoined(joining.gameId)
    }

    fun play(gameId: String, baseTellMessage: BaseTellMessage) {
        val gameArgs = games[gameId] ?: throw IllegalArgumentException("Game not found")
        val game = gameArgs.game

        when (baseTellMessage.actor) {
            gameArgs.player1.name -> deliver(gameArgs.player1, baseTellMessage)
            gameArgs.player2.name -> deliver(gameArgs.player2, baseTellMessage)
            else -> throw IllegalArgumentException("Player not found")
        }
    }

    private fun deliver(player: RemoteWsPlayer, message: BaseTellMessage) {
        return when (message) {
            is TellYesNo -> player.resolveYesNo(message.yes)
            is TellSelectCard -> player.resolveSelectCard(Card.parse(message.card))
            is TellSelectZone -> player.resolveSelectZone(CardZones.valueOf(message.zone))
        }
    }
}

data class GameArgs(
    val player1: RemoteWsPlayer,
    val game: GinRummyGame,
) {
    lateinit var player2: RemoteWsPlayer
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