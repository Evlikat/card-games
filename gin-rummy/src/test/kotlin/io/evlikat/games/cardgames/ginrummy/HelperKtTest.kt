package io.evlikat.games.cardgames.ginrummy

import io.evlikat.games.cardgames.core.Card
import io.evlikat.games.cardgames.core.Suit.*
import io.evlikat.games.cardgames.core.of
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class HelperKtTest {

    @Test
    fun shouldFindSimpleCombinations() {
        val result = findCombinations(
            listOf(
                5 of HEARTS,
                'Q' of HEARTS,
                5 of CLUBS,
                4 of SPADES,
                7 of DIAMONDS,
                2 of SPADES,
                3 of SPADES,
                'K' of HEARTS,
                5 of DIAMONDS,
            )
        )

        assertEquals(
            listOf('Q' of HEARTS, 7 of DIAMONDS, 'K' of HEARTS) to listOf(
                listOf(
                    2 of SPADES,
                    3 of SPADES,
                    4 of SPADES,
                ),
                listOf(
                    5 of HEARTS,
                    5 of CLUBS,
                    5 of DIAMONDS,
                )
            ),
            result
        )
    }

    @Test
    fun shouldFindTwoRunsDifferentSuit() {
        val result = findCombinations(
            listOf(
                4 of HEARTS,
                2 of SPADES,
                4 of SPADES,
                3 of HEARTS,
                3 of SPADES,
                2 of HEARTS,
            )
        )

        assertEquals(
            emptyList<Card>() to listOf(
                listOf(
                    2 of HEARTS,
                    3 of HEARTS,
                    4 of HEARTS,
                ),
                listOf(
                    2 of SPADES,
                    3 of SPADES,
                    4 of SPADES,
                ),
            ),
            result
        )
    }

    @Test
    fun shouldFindTwoRunsSameSuit() {
        val result = findCombinations(
            listOf(
                9 of HEARTS,
                10 of HEARTS,
                3 of HEARTS,
                4 of HEARTS,
                2 of HEARTS,
                8 of HEARTS,
            )
        )

        assertEquals(
            emptyList<Card>() to listOf(
                listOf(
                    2 of HEARTS,
                    3 of HEARTS,
                    4 of HEARTS,
                ),
                listOf(
                    8 of HEARTS,
                    9 of HEARTS,
                    10 of HEARTS,
                ),
            ),
            result
        )
    }

    @Test
    fun shouldFindOptimalDeadwood() {
        val result = findCombinations(
            listOf(
                2 of HEARTS,
                3 of HEARTS,
                4 of HEARTS,
                4 of DIAMONDS,
                4 of CLUBS,
            )
        )

        assertEquals(
            listOf(2 of HEARTS, 3 of HEARTS) to listOf(
                listOf(
                    4 of HEARTS,
                    4 of DIAMONDS,
                    4 of CLUBS,
                ),
            ),
            result
        )
    }

    @Test
    fun shouldFindOptimalDeadwoodTwoIntersections() {
        val result = findCombinations(
            listOf(
                2 of HEARTS,
                3 of HEARTS,
                4 of HEARTS,
                4 of DIAMONDS,
                4 of CLUBS,
                5 of CLUBS,
                6 of CLUBS,
            )
        )

        assertEquals(
            listOf(4 of DIAMONDS) to listOf(
                listOf(
                    2 of HEARTS,
                    3 of HEARTS,
                    4 of HEARTS,
                ),
                listOf(
                    4 of CLUBS,
                    5 of CLUBS,
                    6 of CLUBS,
                ),
            ),
            result
        )
    }

    @Test
    fun shouldFindOptimalDeadwoodBreakLongRun() {
        val result = findCombinations(
            listOf(
                2 of HEARTS,
                3 of HEARTS,
                4 of HEARTS,
                4 of DIAMONDS,
                4 of CLUBS,
                5 of HEARTS,
                6 of HEARTS,
            )
        )

        assertEquals(
            listOf(4 of DIAMONDS, 4 of CLUBS) to listOf(
                listOf(
                    2 of HEARTS,
                    3 of HEARTS,
                    4 of HEARTS,
                    5 of HEARTS,
                    6 of HEARTS,
                ),
            ),
            result
        )
    }
}