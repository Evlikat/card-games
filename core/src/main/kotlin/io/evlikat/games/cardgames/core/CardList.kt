package io.evlikat.games.cardgames.core

interface CardList : List<Card> {

    infix fun append(card: Card): CardList

    infix fun prepend(card: Card): CardList

    infix fun append(other: CardList): CardList

    infix fun prepend(other: CardList): CardList
}