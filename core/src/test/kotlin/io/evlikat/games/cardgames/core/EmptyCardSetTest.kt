package io.evlikat.games.cardgames.core

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class EmptyCardSetTest {

    @Test
    fun shouldEqualsEmptySet() {
        assertTrue(emptySet<Card>() == EmptyCardSet)
        assertTrue(EmptyCardSet == emptySet<Card>())
    }
}