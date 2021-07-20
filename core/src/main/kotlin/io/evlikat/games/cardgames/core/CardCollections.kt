package io.evlikat.games.cardgames.core

fun cards(vararg values: String): CardSet {
    return BitCardSet.of(values.map { Card.parse(it) })
}