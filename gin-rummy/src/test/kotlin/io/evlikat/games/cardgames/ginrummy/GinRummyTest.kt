package io.evlikat.games.cardgames.ginrummy

import com.nhaarman.mockito_kotlin.*
import io.evlikat.games.cardgames.core.Player
import io.evlikat.games.cardgames.core.Watcher
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class GinRummyTest {

    private val watcher: Watcher = mock()

    @Test
    fun hand() {
        val gin = GinRummy(watcher, Player("1"), Player("2"))
        gin.hand()

        assertEquals(10, gin.hand1.cards.size)
        assertEquals(10, gin.hand2.cards.size)
        assertEquals(32, gin.deck.size)

        gin.atStartOfGame()

        assertEquals(1, gin.discard.allCards.size)
        assertEquals(31, gin.deck.size)

        verify(watcher, times(10)).cardMoved(any(), eq(gin.deck), eq(gin.hand1))
        verify(watcher, times(10)).cardMoved(any(), eq(gin.deck), eq(gin.hand2))
        verify(watcher).cardMoved(any(), eq(gin.deck), eq(gin.discard))
    }
}