package io.evlikat.games.cardgames.ginrummy

import io.evlikat.games.cardgames.core.*
import io.evlikat.games.cardgames.core.Nominal.*

class GinRummy(
    private val watcher: Watcher,
    private val player1: Player,
    private val player2: Player,
) {
    private val deck = Deck.standard52().apply {
        shuffle()
    }

    private val hand1 = Hand(player1).also {
        it.watcher = watcher
    }

    private val hand2 = Hand(player2).also {
        it.watcher = watcher
    }

    private val discard = Discard().also {
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

    fun turn(number: Int, activePlayer: Player) {
        val playerHand = if (activePlayer == player1) hand1 else hand2
        if (number == 1) {
            val drawTopDiscard = activePlayer.askYesNo("Do you want to draw top discard card?")
            if (drawTopDiscard) {
                discard.moveCardTo(playerHand)
                val selectedCard = activePlayer.askSelectCard("Select card to discard", playerHand.allCards)
                playerHand.moveCardTo(selectedCard, discard)
            }
        } else {
            val selectedZone = activePlayer.askSelectZone("Draw from deck or discard", deck, discard)
            selectedZone.moveCardTo(playerHand)
            val selectedCard = activePlayer.askSelectCard("Select card to discard", playerHand.allCards)
            playerHand.moveCardTo(selectedCard, discard)
        }
        // TODO: add auto calc
        val knock = activePlayer.askYesNo("Do you want to knock?")
        if (!knock) {
            return
        }
        val discardCover = activePlayer.askSelectCard("Select card to cover discard", playerHand.allCards)
        playerHand.moveCardTo(discardCover, discard)

        val anotherPlayer = if (activePlayer == player1) player2 else player1
    }
}