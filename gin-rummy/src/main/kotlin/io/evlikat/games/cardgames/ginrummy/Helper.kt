package io.evlikat.games.cardgames.ginrummy

import io.evlikat.games.cardgames.core.Card
import io.evlikat.games.cardgames.core.Nominal.*

fun findCombinations(cards: Collection<Card>): Pair<List<Card>, List<List<Card>>> {
    val allCards = cards.toSet()

    val runs = findRuns(cards)
    val sets = findSets(cards)

    val contestedCards = runs.flatMap { it.cards } intersect sets.flatMap { it.cards }
    if (contestedCards.isEmpty()) {
        val deadwood = allCards - sets.flatMap { it.cards } - runs.flatMap { it.cards }
        return deadwood.toList() to (runs.map { it.cards } + sets.map { it.cards })
    }
    val points = contestedCards.associateWith { contestedCard ->
        val contestedRun = runs.find { it.contains(contestedCard) }!!
        val contestedSet = sets.find { it.contains(contestedCard) }!!
        contestedRun to contestedSet
    }
    return points.keys.powerSet().map { cardsInRuns ->
        val cardsInSets = points.keys - cardsInRuns
        val newRuns = runs.toMutableList()
        val newSets = sets.toMutableList()
        for (contestedCard in cardsInRuns) {
            val (_, set) = points.getValue(contestedCard)
            newSets.remove(set)
            val setCandidate = set.without(contestedCard)
            if (setCandidate.isValid()) {
                newSets.add(setCandidate)
            }
        }
        for (contestedCard in cardsInSets) {
            val (run, _) = points.getValue(contestedCard)
            newRuns.remove(run)
            val runCandidate = run.without(contestedCard)
            if (runCandidate.isValid()) {
                newRuns.add(runCandidate)
            }
        }
        val potentialDeadwood = (allCards - newSets.flatMap { it.cards } - newRuns.flatMap { it.cards }).toList()
        val evaluated = evaluate(potentialDeadwood)
        evaluated to (potentialDeadwood to (newRuns.map { it.cards } + newSets.map { it.cards }))
    }.minByOrNull { it.first }!!.second
}

private data class GRun(val cards: List<Card>) {
    fun contains(card: Card): Boolean = cards.contains(card)
    fun without(card: Card): GRun = GRun(cards.filter { it != card })
    fun isValid(): Boolean =
        (cards.last().nominal.ordinal - cards.first().nominal.ordinal == cards.size + 1) && cards.size >= 3
}

private data class GSet(val cards: List<Card>) {
    fun contains(card: Card): Boolean = cards.contains(card)
    fun without(card: Card): GSet = GSet(cards.filter { it != card })
    fun isValid(): Boolean = cards.size >= 3
}

private fun findRuns(cards: Collection<Card>): List<GRun> {
    return cards
        .groupBy { it.suit }
        .mapValues { (_, suitCards) -> suitCards.sortedBy { it.nominal.ordinal } }
        .map { (_, suitCards) ->
            suitCards
                .fold(mutableListOf<MutableList<Card>>()) { acc, card ->
                    val lastRun = acc.lastOrNull() ?: mutableListOf<Card>().also { acc.add(it) }
                    if (lastRun.isEmpty() || lastRun.last().precedes(card)) {
                        lastRun.add(card)
                    } else {
                        acc.add(mutableListOf(card))
                    }
                    acc
                }
                .filter { it.size >= 3 }
                .map { GRun(it) }
        }
        .flatten()
}

private fun findSets(cards: Collection<Card>): List<GSet> {
    return cards
        .groupBy { it.nominal }
        .filterValues { it.size >= 3 }
        .values
        .map { GSet(it) }
}

private fun evaluate(cards: Collection<Card>): Int = cards.sumOf { evaluate(it) }

private fun evaluate(card: Card): Int {
    return when (card.nominal) {
        C10, J, Q, K -> 10
        else -> card.nominal.ordinal + 1
    }
}

private fun <T> Collection<T>.powerSet(): Set<Set<T>> =
    if (isEmpty()) setOf(emptySet())
    else drop(1)
        .powerSet()
        .let { it + it.map { it + first() } }