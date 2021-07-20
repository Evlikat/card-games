package io.evlikat.games.cardgames.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ByteArrayCardListTest {

    @Test
    fun getSize() {
        assertThat(cards("2♥", "4♣")).hasSize(2)
    }

    @Test
    fun append() {
        val append = cards("2♥", "4♣").append(card("4♥"))
        assertThat(append).isEqualTo(cards("2♥", "4♣", "4♥"))
    }

    @Test
    fun appendList() {
        val append = cards("2♥", "4♣").append(cards("4♥", "5♥"))
        assertThat(append).isEqualTo(cards("2♥", "4♣", "4♥", "5♥"))
    }

    @Test
    fun prepend() {
        val prepend = cards("2♥", "4♣").prepend(card("4♥"))
        assertThat(prepend).isEqualTo(cards("4♥", "2♥", "4♣"))
    }

    @Test
    fun prependList() {
        val prepend = cards("2♥", "4♣").prepend(cards("4♥", "5♥"))
        assertThat(prepend).isEqualTo(cards("4♥", "5♥", "2♥", "4♣"))
    }

    @Test
    fun contains() {
        assertThat(cards("2♥", "4♣").contains(card("2♥"))).isTrue
        assertThat(cards("2♥", "4♣").contains(card("4♥"))).isFalse
    }

    @Test
    fun containsAll() {
        assertThat(cards("2♥", "4♣").containsAll(cards("2♥", "4♣"))).isTrue
        assertThat(cards("2♥", "4♣").containsAll(cards("4♣"))).isTrue
        assertThat(cards("2♥", "4♣").containsAll(cards("4♥"))).isFalse
    }

    @Test
    fun get() {
        val byIndex = cards("2♥", "4♣")[1]
        assertThat(byIndex).isEqualTo(card("4♣"))
    }

    @Test
    fun indexOf() {
        val indexOf = cards("2♥", "4♣").indexOf(card("2♥"))
        assertThat(indexOf).isEqualTo(0)
    }

    @Test
    fun isEmpty() {
        assertThat(cards("2♥", "4♣").isEmpty()).isFalse
        assertThat(cards().isEmpty()).isTrue
    }

    @Test
    fun lastIndexOf() {
        val lastIndexOf = cards("2♥", "4♣").lastIndexOf(card("2♥"))
        assertThat(lastIndexOf).isEqualTo(0)
    }

    @Test
    operator fun iterator() {
        val lit = cards("2♥", "4♣").iterator()
        assertThat(lit.hasNext()).isTrue

        assertThat(lit.next()).isEqualTo(card("2♥"))
        assertThat(lit.hasNext()).isTrue

        assertThat(lit.next()).isEqualTo(card("4♣"))
        assertThat(lit.hasNext()).isFalse
    }

    @Test
    fun listIterator() {
        val lit = cards("2♥", "4♣").listIterator()
        assertThat(lit.hasPrevious()).isFalse
        assertThat(lit.hasNext()).isTrue

        assertThat(lit.next()).isEqualTo(card("2♥"))
        assertThat(lit.hasPrevious()).isTrue
        assertThat(lit.hasNext()).isTrue

        assertThat(lit.next()).isEqualTo(card("4♣"))
        assertThat(lit.hasPrevious()).isTrue
        assertThat(lit.hasNext()).isFalse
    }

    @Test
    fun listIteratorFromIndex() {
        val lit = cards("2♣", "2♥", "4♣").listIterator(1)
        assertThat(lit.hasPrevious()).isTrue
        assertThat(lit.hasNext()).isTrue

        assertThat(lit.next()).isEqualTo(card("2♥"))
        assertThat(lit.hasPrevious()).isTrue
        assertThat(lit.hasNext()).isTrue

        assertThat(lit.next()).isEqualTo(card("4♣"))
        assertThat(lit.hasPrevious()).isTrue
        assertThat(lit.hasNext()).isFalse
    }

    @Test
    fun subList() {
        assertThat(cards("2♣", "2♥", "4♣", "4♥").subList(1, 3)).isEqualTo(cards("2♥", "4♣"))
    }

    private fun card(value: String): Card {
        return Card.parse(value)
    }

    private fun cards(vararg values: String): ByteArrayCardList {
        return ByteArrayCardList.of(values.map { Card.parse(it) })
    }
}