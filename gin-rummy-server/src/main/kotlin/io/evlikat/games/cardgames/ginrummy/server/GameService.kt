package io.evlikat.games.cardgames.ginrummy.server

import io.evlikat.games.cardgames.ginrummy.GinRummyGame
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class GameService {

    private val log = LoggerFactory.getLogger(GameService::class.java)

    private val games = ConcurrentHashMap<String, GinRummyGame>()

    fun createNewGame(creation: GameCreation): GameCreated {
        val game = GinRummyGame()
        val gameId = UUID.randomUUID().toString()
        games[gameId] = game

        log.info("Game $gameId created by ${creation.playerName}")

        return GameCreated(gameId = gameId)
    }

    fun joinGame(joining: GameJoining) {

    }
}

data class GameCreation(
    val playerName: String
)

data class GameJoining(
    val playerName: String,
)

data class GameCreated(
    val gameId: String
)