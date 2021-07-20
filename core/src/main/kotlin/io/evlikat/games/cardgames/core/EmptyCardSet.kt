package io.evlikat.games.cardgames.core

object EmptyCardSet : CardSet {

    override fun union(other: CardSet): CardSet = other

    override fun intersect(other: CardSet): CardSet = EmptyCardSet

    override fun minus(other: CardSet): CardSet = EmptyCardSet

    override fun minus(card: Card): CardSet = EmptyCardSet

    override fun plus(card: Card): CardSet = BitCardSet.of(card)

    override val size: Int = 0

    override fun contains(element: Card): Boolean = false

    override fun containsAll(elements: Collection<Card>): Boolean = false

    override fun isEmpty(): Boolean = true

    override fun iterator(): Iterator<Card> = EmptyIterator

    object EmptyIterator : Iterator<Card> {
        override fun hasNext(): Boolean = false
        override fun next(): Card = throw NoSuchElementException("The set is empty")
    }

    override fun equals(other: Any?): Boolean = other is Set<*> && other.isEmpty()

    override fun hashCode(): Int = 0
}