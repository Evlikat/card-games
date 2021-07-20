package io.evlikat.games.cardgames.core

interface CardSet : Set<Card> {

    infix fun union(other: CardSet): CardSet

    infix fun intersect(other: CardSet): CardSet

    operator fun minus(other: CardSet): CardSet

    operator fun plus(card: Card): CardSet

    operator fun minus(card: Card): CardSet
}