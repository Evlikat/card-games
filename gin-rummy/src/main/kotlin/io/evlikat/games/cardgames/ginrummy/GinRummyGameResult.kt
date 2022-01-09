package io.evlikat.games.cardgames.ginrummy

data class GinRummyGameResult(
    val player1Score: Int,
    val player2Score: Int
) {
    val winnerPlayerIndex: Int
        get() = if (player1Score > player2Score) 0 else if (player1Score < player2Score) 1 else -1
}