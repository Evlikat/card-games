package io.evlikat.games.cardgames.ginrummy.server

import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class GameController(
    private val gameService: GameService,
    private val simp: SimpMessagingTemplate
) {

    @MessageMapping("/game/new")
    fun createNewGame(message: HelloMessage) {
        val gameCreated = gameService.createNewGame(
            GameCreation(playerName = message.name, clientId = message.clientId)
        )
        simp.convertAndSend("/client/${message.clientId}", GameCreatedMessage(gameId = gameCreated.gameId))
    }

    @MessageMapping("/game/{gameId}")
    fun joinGame(@DestinationVariable gameId: String, message: HelloMessage) {
        val gameJoin = gameService.joinGame(
            GameJoining(gameId = gameId, playerName = message.name, clientId = message.clientId)
        )
        simp.convertAndSend("/game/$gameId", GameStartedMessage(gameId = gameId))
    }

    @MessageMapping("/game/{gameId}/play")
    fun play(@DestinationVariable gameId: String, message: BaseTellMessage) {
        gameService.play(gameId, message)
    }
}

data class GameCreatedMessage(val gameId: String)
data class GameStartedMessage(val gameId: String)
data class HelloMessage(val name: String, val clientId: String)
