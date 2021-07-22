package io.evlikat.games.cardgames.ginrummy

import io.evlikat.games.cardgames.core.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class HelperKtTest {

    @Test
    fun shouldFindSimpleCombinations() {
        val result = findCombinations(cards("5♥", "Q♥", "5♣", "4♠", "7♦", "2♠", "3♠", "K♥", "5♦"))

        assertEquals(
            cards("Q♥", "7♦", "K♥") to listOf(gRun("2♠", "3♠", "4♠"), gSet("5♥", "5♣", "5♦")),
            result
        )
    }

    @Test
    fun shouldFindTwoRunsDifferentSuit() {
        val result = findCombinations(cards("4♥", "2♠", "4♠", "3♥", "3♠", "2♥"))

        assertEquals(
            EmptyCardSet to listOf(
                gRun("2♠", "3♠", "4♠"),
                gRun("2♥", "3♥", "4♥"),
            ),
            result
        )
    }

    @Test
    fun shouldFindTwoRunsSameSuit() {
        val result = findCombinations(cards("4♥", "8♥", "9♥", "3♥", "2♥", "10♥"))

        assertEquals(
            EmptyCardSet to listOf(
                gRun("2♥", "3♥", "4♥"),
                gRun("8♥", "9♥", "10♥"),
            ),
            result
        )
    }

    @Test
    fun shouldFindOptimalDeadwood() {
        val result = findCombinations(cards("4♥", "2♥", "4♣", "3♥", "4♠"))

        assertEquals(
            cards("2♥", "3♥") to listOf(gSet("4♥", "4♣", "4♠")),
            result
        )
    }

    @Test
    fun shouldFindOptimalDeadwoodTwoIntersections() {
        val result = findCombinations(cards("4♥", "5♣", "2♥", "4♣", "3♥", "4♠", "6♣"))

        assertEquals(
            cards("4♠") to listOf(gRun("4♣", "5♣", "6♣"), gRun("2♥", "3♥", "4♥")),
            result
        )
    }

    @Test
    fun shouldFindOptimalDeadwoodBreakLongRun() {
        val result = findCombinations(cards("4♥", "5♥", "2♥", "4♣", "3♥", "4♠", "6♥"))

        assertEquals(
            cards("4♠", "4♣") to listOf(gRun("2♥", "3♥", "4♥", "5♥", "6♥")),
            result
        )
    }

    private fun gRun(vararg values: String): GRun {
        return GRun(BitCardSet.of(values.map { Card.parse(it) }))
    }

    private fun gSet(vararg values: String): GSet {
        return GSet(BitCardSet.of(values.map { Card.parse(it) }))
    }
}