package io.evlikat.games.cardgames.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class BitCardSetTest {

    @Test
    fun union() {
        val set = cards("2♥", "4♣") union cards("3♥", "4♣")
        assertThat(set).isEqualTo(cards("2♥", "4♣", "3♥"))
    }

    @Test
    fun intersect() {
        val set = cards("2♥", "4♣") intersect cards("3♥", "4♣")
        assertThat(set).isEqualTo(cards("4♣"))
    }

    @Test
    fun plus() {
        val set = cards("2♥", "4♣") + card("3♥")
        assertThat(set).isEqualTo(cards("2♥", "4♣", "3♥"))
    }

    @Test
    fun plusCardSet() {
        val set = cards("2♥", "4♣") + cards("3♥")
        assertThat(set).isEqualTo(cards("2♥", "4♣", "3♥"))
    }

    @Test
    fun minusCardSet() {
        val set = cards("2♥", "4♣") - cards("3♥", "4♣")
        assertThat(set).isEqualTo(cards("2♥"))
    }

    @Test
    fun minusCard() {
        val set = cards("2♥", "4♣") - card("4♣")
        assertThat(set).isEqualTo(cards("2♥"))
    }

    @Test
    fun getSize() {
        assertThat(cards("2♥", "4♣")).hasSize(2)
    }

    @Test
    fun contains() {
        assertThat(cards("2♥", "4♣")).contains(card("2♥"))
    }

    @Test
    fun containsAll() {
        assertThat(cards("2♥", "4♣").containsAll(listOf(card("2♥"), card("4♣")))).isTrue
    }

    @Test
    fun containsNotAll() {
        assertThat(cards("2♥", "4♣").containsAll(listOf(card("2♥"), card("3♣")))).isFalse
    }

    @Test
    fun isNotEmpty() {
        assertThat(cards("2♥", "4♣")).isNotEmpty
    }

    @Test
    fun isEmpty() {
        assertThat(cards()).isEmpty()
    }

    @Test
    fun iterator() {
        val i = cards("2♥", "4♣").iterator()
        assertThat(i.hasNext()).isTrue
        assertThat(i.next()).isEqualTo(card("4♣"))
        assertThat(i.next()).isEqualTo(card("2♥"))
        assertThat(i.hasNext()).isFalse
    }

    @Test
    fun testEquals() {
        assertThat(cards("2♥", "4♣") == cards("2♥", "4♣")).isTrue
    }

    private fun card(value: String): Card {
        return Card.parse(value)
    }

    private fun cards(vararg values: String): CardSet {
        return BitCardSet.of(values.map { Card.parse(it) })
    }
}