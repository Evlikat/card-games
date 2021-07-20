package io.evlikat.games.cardgames.core

class ByteArrayCardList private constructor(private val elements: ByteArray) : CardList {

    override val size: Int = elements.size

    companion object {
        fun of(cards: Collection<Card>): ByteArrayCardList {
            val byteArray = ByteArray(cards.size)
            var i = 0
            for (card in cards) {
                byteArray[i++] = card.ordinal.toByte()
            }
            return ByteArrayCardList(byteArray)
        }

        fun of(vararg cards: Card): ByteArrayCardList {
            val byteArray = ByteArray(cards.size)
            for (i in cards.indices) {
                byteArray[i] = cards[i].ordinal.toByte()
            }
            return ByteArrayCardList(byteArray)
        }
    }

    override fun append(card: Card): CardList {
        val newElements = elements.copyOf(elements.size + 1)
        newElements[newElements.lastIndex] = card.ordinal.toByte()
        return ByteArrayCardList(newElements)
    }

    override fun append(other: CardList): CardList {
        if (isEmpty()) {
            return other
        }
        val newElements = elements.copyOf(elements.size + other.size)
        for (i in elements.size until newElements.size) {
            newElements[i] = other[i - elements.size].ordinal.toByte()
        }
        return ByteArrayCardList(newElements)
    }

    override fun prepend(card: Card): CardList {
        val newElements = ByteArray(elements.size + 1)
        System.arraycopy(elements, 0, newElements, 1, elements.size)
        newElements[0] = card.ordinal.toByte()
        return ByteArrayCardList(newElements)
    }

    override fun prepend(other: CardList): CardList {
        if (isEmpty()) {
            return other
        }
        val newElements = ByteArray(elements.size + other.size)
        System.arraycopy(elements, 0, newElements, other.size, elements.size)
        for (i in 0 until other.size) {
            newElements[i] = other[i].ordinal.toByte()
        }
        return ByteArrayCardList(newElements)
    }

    override fun contains(element: Card): Boolean = this.elements.find { it == element.ordinal.toByte() } != null

    override fun containsAll(elements: Collection<Card>): Boolean =
        elements.all { element -> this.elements.find { it == element.ordinal.toByte() } != null }

    override operator fun get(index: Int): Card = Card.values()[elements[index].toInt()]

    override fun indexOf(element: Card): Int = elements.indexOf(element.ordinal.toByte())

    override fun isEmpty(): Boolean = elements.isEmpty()

    override fun iterator(): Iterator<Card> {
        return ByteArrayCardIterator()
    }

    override fun lastIndexOf(element: Card): Int = elements.lastIndexOf(element.ordinal.toByte())

    override fun listIterator(): ListIterator<Card> {
        return ByteArrayCardListIterator(0)
    }

    override fun listIterator(index: Int): ListIterator<Card> {
        return ByteArrayCardListIterator(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): ByteArrayCardList {
        return ByteArrayCardList(elements.copyOfRange(fromIndex, toIndex))
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            null -> false
            is ByteArrayCardList -> other.elements.contentEquals(this.elements)
            is CardList -> this.size == other.size && this.zip(other).all { (t, o) -> t == o }
            is List<*> -> {
                if (this.size == other.size && other.all { it is Card }) {
                    other as List<Card>
                    this.zip(other).all { (t, o) -> t == o }
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

    inner class ByteArrayCardIterator : Iterator<Card> {
        private var seen = 0
        override fun hasNext(): Boolean = seen < elements.size
        override fun next(): Card = Card.values()[elements[seen++].toInt()]
    }

    inner class ByteArrayCardListIterator(index: Int) : ListIterator<Card> {
        private var seen = index
        override fun hasNext(): Boolean = seen < elements.size
        override fun next(): Card = Card.values()[elements[seen++].toInt()]
        override fun hasPrevious(): Boolean = seen > 0
        override fun previousIndex(): Int = seen - 1
        override fun nextIndex(): Int = seen
        override fun previous(): Card = Card.values()[elements[seen--].toInt()]
    }
}