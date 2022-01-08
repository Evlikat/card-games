package io.evlikat.games.cardgames.ginrummy

import com.nhaarman.mockito_kotlin.*
import io.evlikat.games.cardgames.core.Player
import io.evlikat.games.cardgames.core.Watcher
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class GinRummyTest {

    private val watcher: Watcher = mock()
    private val player1: Player = mock()
    private val player2: Player = mock()

    @Test
    fun hand() {
        val gin = GinRummy(watcher, player1, player2)
        gin.hand()

        assertEquals(10, gin.hand1.cards.size)
        assertEquals(10, gin.hand2.cards.size)
        assertEquals(32, gin.deck.size)

        gin.atStartOfGame()

        assertEquals(1, gin.discard.size)
        assertEquals(31, gin.deck.size)

        verify(watcher, times(10)).cardMoved(any(), eq(gin.deck), eq(gin.hand1))
        verify(watcher, times(10)).cardMoved(any(), eq(gin.deck), eq(gin.hand2))
        verify(watcher).cardMoved(any(), eq(gin.deck), eq(gin.discard))
    }
}