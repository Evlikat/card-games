package io.evlikat.games.cardgames.ginrummy

import io.evlikat.games.cardgames.core.*
import io.evlikat.games.cardgames.core.CardZones.*

class GinRummy(
    private val watcher: Watcher,
    private val player1: Player,
    private val player2: Player,
) {
    val deck = Deck.standard52().also {
        it.watcher = watcher
    }.apply {
        shuffle()
    }

    val hand1 = Hand(player1, HAND_1).also {
        it.watcher = watcher
    }

    val hand2 = Hand(player2, HAND_2).also {
        it.watcher = watcher
    }

    val discard = Discard().also {
        it.watcher = watcher
    }

    fun hand() {
        repeat(times = 10) {
            deck.moveCardTo(hand1)
            deck.moveCardTo(hand2)
        }
    }

    fun atStartOfGame() {
        deck.moveCardTo(discard)
    }

    suspend fun play(): GinRummyGameResult {
        var turnNumber = 1
        var state: GameState = PlayerOneChooseDiscard
        while (deck.size > 2) {
            val playerNumber = PlayerNumber.values()[(turnNumber - 1) % 2]
            val activePlayer = if (playerNumber == PlayerNumber.ONE) player1 else player2
            state = turn(state, activePlayer)
            if (state is GameOver) {
                val gameResult = gameResult(playerNumber, state.deadwoodDiff)
                watcher.gameOver(
                    GameResult(
                        winnerPlayerIndex = gameResult.winnerPlayerIndex,
                        scores = listOf(gameResult.player1Score, gameResult.player2Score)
                    )
                )
                return gameResult
            }
            turnNumber++
        }
        val (deadwood1, _) = findCombinations(hand1.cards)
        val (deadwood2, _) = findCombinations(hand2.cards)
        val deadwoodValue1 = evaluate(deadwood1)
        val deadwoodValue2 = evaluate(deadwood2)
        return when {
            deadwoodValue1 > deadwoodValue2 -> GinRummyGameResult(0, deadwoodValue1 - deadwoodValue2)
            deadwoodValue1 < deadwoodValue2 -> GinRummyGameResult(deadwoodValue2 - deadwoodValue1, 0)
            else -> GinRummyGameResult(0, 0)
        }
    }

    private fun gameResult(playerNumber: PlayerNumber, deadwoodDiff: Int): GinRummyGameResult {
        return if (deadwoodDiff > 0) {
            if (playerNumber == PlayerNumber.ONE) {
                GinRummyGameResult(KNOCK_BONUS + deadwoodDiff, 0)
            } else {
                GinRummyGameResult(0, KNOCK_BONUS + deadwoodDiff)
            }
        } else {
            if (playerNumber == PlayerNumber.TWO) {
                GinRummyGameResult(0, KNOCK_BONUS - deadwoodDiff)
            } else {
                GinRummyGameResult(KNOCK_BONUS - deadwoodDiff, 0)
            }
        }
    }

    private suspend fun turn(gameState: GameState, activePlayer: Player): GameState {
        val playerHand = if (activePlayer == player1) hand1 else hand2
        when (gameState) {
            PlayerOneChooseDiscard -> {
                val drawTopDiscard = activePlayer.askYesNo("Do you want to draw top discard card?")
                if (drawTopDiscard) {
                    discard.moveCardTo(playerHand)
                    val selectedCard = activePlayer.askSelectCard("Select card to discard", playerHand.cards)
                    playerHand.moveCardTo(selectedCard, discard)
                } else {
                    return PlayerTwoChooseDiscard
                }
            }
            PlayerTwoChooseDiscard -> {
                val drawTopDiscard = activePlayer.askYesNo("Do you want to draw top discard card?")
                if (drawTopDiscard) {
                    discard.moveCardTo(playerHand)
                    val selectedCard = activePlayer.askSelectCard("Select card to discard", playerHand.cards)
                    playerHand.moveCardTo(selectedCard, discard)
                } else {
                    return BothPlayersDeclinedDiscard
                }
            }
            BothPlayersDeclinedDiscard -> {
                deck.moveCardTo(playerHand)
                val selectedCard = activePlayer.askSelectCard("Select card to discard", playerHand.cards)
                playerHand.moveCardTo(selectedCard, discard)
            }
            else -> {
                val selectedZone = zone(
                    activePlayer.askSelectZone("Draw from deck or discard", deck.cardZones, discard.cardZones)
                )
                selectedZone.moveCardTo(playerHand)
                val selectedCard = activePlayer.askSelectCard("Select card to discard", playerHand.cards)
                playerHand.moveCardTo(selectedCard, discard)
            }
        }
        val (deadwood, combinations) = findCombinations(playerHand.cards)
        if (deadwood.isNotEmpty() && deadwood.map { evaluate(it) }.drop(1).sum() > MAX_DEADWOOD_VALUE) {
            return EndTurn
        }

        val knock = activePlayer.askYesNo("Do you want to knock?")
        if (!knock) {
            return EndTurn
        }

        val cardsAvailableToDiscard = playerHand.cards.filter { playerCard ->
            val (potentialDeadwood, _) = findCombinations(playerHand.cards - playerCard)
            potentialDeadwood.isEmpty() || evaluate(potentialDeadwood) <= MAX_DEADWOOD_VALUE
        }

        val discardCover = activePlayer.askSelectCard("Select card to cover discard", cardsAvailableToDiscard)
        playerHand.moveCardTo(discardCover, discard)

        val deadwoodValueAfterDiscard = evaluate(deadwood - discardCover)

        val anotherPlayerHand = if (activePlayer == player1) hand2 else hand1

        val (anotherPlayerDeadwood, _) = findCombinations(anotherPlayerHand.cards)

        val (completedCombinations, anotherPlayerNewDeadwood) = completeCombinations(
            combinations,
            anotherPlayerDeadwood
        )

        val anotherPlayerDeadwoodValue = anotherPlayerNewDeadwood.map { evaluate(it) }.drop(1).sum()

        return GameOver(deadwoodDiff = anotherPlayerDeadwoodValue - deadwoodValueAfterDiscard)
    }

    private fun zone(zones: CardZones): CardZone = when (zones) {
        DECK -> deck
        DISCARD -> discard
        HAND_1 -> hand1
        HAND_2 -> hand2
    }

    companion object {
        const val MAX_DEADWOOD_VALUE = 10
        const val KNOCK_BONUS = 25
    }
}

sealed class GameState
object PlayerOneChooseDiscard : GameState()
object PlayerTwoChooseDiscard : GameState()
object BothPlayersDeclinedDiscard : GameState()
object EndTurn : GameState()
class GameOver(val deadwoodDiff: Int) : GameState()
