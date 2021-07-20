package io.evlikat.games.cardgames.core

enum class Card(val suit: Suit, val nominal: Nominal) {
    SPADES_A(Suit.SPADES, Nominal.A),
    SPADES_2(Suit.SPADES, Nominal.C2),
    SPADES_3(Suit.SPADES, Nominal.C3),
    SPADES_4(Suit.SPADES, Nominal.C4),
    SPADES_5(Suit.SPADES, Nominal.C5),
    SPADES_6(Suit.SPADES, Nominal.C6),
    SPADES_7(Suit.SPADES, Nominal.C7),
    SPADES_8(Suit.SPADES, Nominal.C8),
    SPADES_9(Suit.SPADES, Nominal.C9),
    SPADES_10(Suit.SPADES, Nominal.C10),
    SPADES_J(Suit.SPADES, Nominal.J),
    SPADES_Q(Suit.SPADES, Nominal.Q),
    SPADES_K(Suit.SPADES, Nominal.K),
    CLUBS_A(Suit.CLUBS, Nominal.A),
    CLUBS_2(Suit.CLUBS, Nominal.C2),
    CLUBS_3(Suit.CLUBS, Nominal.C3),
    CLUBS_4(Suit.CLUBS, Nominal.C4),
    CLUBS_5(Suit.CLUBS, Nominal.C5),
    CLUBS_6(Suit.CLUBS, Nominal.C6),
    CLUBS_7(Suit.CLUBS, Nominal.C7),
    CLUBS_8(Suit.CLUBS, Nominal.C8),
    CLUBS_9(Suit.CLUBS, Nominal.C9),
    CLUBS_10(Suit.CLUBS, Nominal.C10),
    CLUBS_J(Suit.CLUBS, Nominal.J),
    CLUBS_Q(Suit.CLUBS, Nominal.Q),
    CLUBS_K(Suit.CLUBS, Nominal.K),
    DIAMONDS_A(Suit.DIAMONDS, Nominal.A),
    DIAMONDS_2(Suit.DIAMONDS, Nominal.C2),
    DIAMONDS_3(Suit.DIAMONDS, Nominal.C3),
    DIAMONDS_4(Suit.DIAMONDS, Nominal.C4),
    DIAMONDS_5(Suit.DIAMONDS, Nominal.C5),
    DIAMONDS_6(Suit.DIAMONDS, Nominal.C6),
    DIAMONDS_7(Suit.DIAMONDS, Nominal.C7),
    DIAMONDS_8(Suit.DIAMONDS, Nominal.C8),
    DIAMONDS_9(Suit.DIAMONDS, Nominal.C9),
    DIAMONDS_10(Suit.DIAMONDS, Nominal.C10),
    DIAMONDS_J(Suit.DIAMONDS, Nominal.J),
    DIAMONDS_Q(Suit.DIAMONDS, Nominal.Q),
    DIAMONDS_K(Suit.DIAMONDS, Nominal.K),
    HEARTS_A(Suit.HEARTS, Nominal.A),
    HEARTS_2(Suit.HEARTS, Nominal.C2),
    HEARTS_3(Suit.HEARTS, Nominal.C3),
    HEARTS_4(Suit.HEARTS, Nominal.C4),
    HEARTS_5(Suit.HEARTS, Nominal.C5),
    HEARTS_6(Suit.HEARTS, Nominal.C6),
    HEARTS_7(Suit.HEARTS, Nominal.C7),
    HEARTS_8(Suit.HEARTS, Nominal.C8),
    HEARTS_9(Suit.HEARTS, Nominal.C9),
    HEARTS_10(Suit.HEARTS, Nominal.C10),
    HEARTS_J(Suit.HEARTS, Nominal.J),
    HEARTS_Q(Suit.HEARTS, Nominal.Q),
    HEARTS_K(Suit.HEARTS, Nominal.K)
    ;

    override fun toString(): String = "${nominal.sign}${suit.sign}"
    fun precedes(card: Card): Boolean = nominal.ordinal + 1 == card.nominal.ordinal

    companion object {
        fun parse(value: String): Card {
            val suitStr = when (value.last()) {
                '♦' -> Suit.DIAMONDS
                '♥' -> Suit.HEARTS
                '♣' -> Suit.CLUBS
                '♠' -> Suit.SPADES
                else -> throw IllegalArgumentException("Unrecognized suit")
            }.toString()
            return valueOf("${suitStr}_${value.dropLast(1)}")
        }
    }
}