package io.evlikat.games.cardgames.core

import java.lang.Long.bitCount
import java.lang.Long.numberOfTrailingZeros

class BitCardSet private constructor(private val elements: Long) : CardSet {

    override fun union(other: CardSet): CardSet = BitCardSet(elements or toElementBits(other))

    override fun intersect(other: CardSet): CardSet = BitCardSet(elements and toElementBits(other))

    override operator fun plus(card: Card): CardSet = BitCardSet(elements or toElementBit(card))

    override operator fun minus(other: CardSet): CardSet = BitCardSet(elements and toElementBits(other).inv())

    override operator fun minus(card: Card): BitCardSet = BitCardSet(elements and toElementBit(card).inv())

    override val size: Int get() = bitCount(elements)

    override fun contains(element: Card): Boolean = elements and toElementBit(element) != 0L

    override fun containsAll(elements: Collection<Card>): Boolean {
        val collectionToElementBits = collectionToElementBits(elements)
        return this.elements and collectionToElementBits == collectionToElementBits
    }

    override fun isEmpty(): Boolean = elements == 0L

    override fun iterator(): Iterator<Card> = BitCardSetIterator()

    override fun toString(): String = joinToString(",", prefix = "[", postfix = "]")

    @SuppressWarnings("unchecked")
    override fun equals(other: Any?): Boolean {
        return when (other) {
            null -> false
            is BitCardSet -> other.elements == this.elements
            is CardSet -> collectionToElementBits(other) == this.elements
            is Set<*> -> {
                if (other.all { it is Card }) {
                    other as Set<Card>
                    collectionToElementBits(other) == this.elements
                } else false
            }
            else -> false
        }
    }

    override fun hashCode(): Int {
        var h = 0
        val i = iterator()
        while (i.hasNext()) {
            h += i.next().hashCode()
        }
        return h
    }

    companion object {

        fun of(vararg card: Card): BitCardSet = BitCardSet(collectionToElementBits(card.toList()))

        fun of(cards: Collection<Card>): BitCardSet = BitCardSet(collectionToElementBits(cards))

        private fun toElementBit(card: Card): Long {
            return 0x01L shl card.ordinal
        }

        private fun toElementBits(cards: CardSet): Long {
            return if (cards is BitCardSet) cards.elements else collectionToElementBits(cards)
        }

        private fun collectionToElementBits(cards: Collection<Card>): Long {
            return cards.fold(0L) { acc, card -> acc or toElementBit(card) }
        }
    }

    inner class BitCardSetIterator(private var unseen: Long = elements) : Iterator<Card> {

        private var lastReturned: Long = 0

        override fun hasNext(): Boolean {
            return unseen != 0L
        }

        override fun next(): Card {
            if (unseen == 0L) throw NoSuchElementException()
            lastReturned = unseen and -unseen
            unseen -= lastReturned
            return Card.values()[numberOfTrailingZeros(lastReturned)]
        }
    }
}