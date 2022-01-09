package io.evlikat.games.cardgames.core

data class GameResult(
    val winnerPlayerIndex: Int,
    val scores: List<Int>
)