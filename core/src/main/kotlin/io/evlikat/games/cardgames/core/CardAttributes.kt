package io.evlikat.games.cardgames.core

enum class SuitColor {
    BLACK,
    RED,
}

enum class Suit(val sign: String, val color: SuitColor) {
    SPADES("♠", SuitColor.BLACK),
    CLUBS("♣", SuitColor.BLACK),
    DIAMONDS("♦", SuitColor.RED),
    HEARTS("♥", SuitColor.RED),
}

enum class Nominal(val sign: String) {
    A("A"),
    C2("2"),
    C3("3"),
    C4("4"),
    C5("5"),
    C6("6"),
    C7("7"),
    C8("8"),
    C9("9"),
    C10("10"),
    J("J"),
    Q("Q"),
    K("K")
}