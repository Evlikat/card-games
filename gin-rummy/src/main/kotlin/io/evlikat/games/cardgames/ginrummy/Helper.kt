package io.evlikat.games.cardgames.ginrummy

import io.evlikat.games.cardgames.core.BitCardSet
import io.evlikat.games.cardgames.core.Card
import io.evlikat.games.cardgames.core.CardSet
import io.evlikat.games.cardgames.core.EmptyCardSet
import io.evlikat.games.cardgames.core.Nominal.*

fun findCombinations(cards: CardSet): Pair<CardSet, List<CardSet>> {
    val allCards = BitCardSet.of(cards)

    val runs = findRuns(cards)
    val sets = findSets(cards)

    val contestedCards = runs.reduce() intersect sets.reduce()
    if (contestedCards.isEmpty()) {
        val deadwood = allCards - sets.reduce() - runs.reduce()
        return deadwood to (runs + sets)
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
            val setCandidate = set.minus(contestedCard)
            if (setCandidate.isValid()) {
                newSets.add(setCandidate)
            }
        }
        for (contestedCard in cardsInSets) {
            val (run, _) = points.getValue(contestedCard)
            newRuns.remove(run)
            val runCandidate = run.minus(contestedCard)
            if (runCandidate.isValid()) {
                newRuns.add(runCandidate)
            }
        }
        val potentialDeadwood = allCards - newSets.reduce() - newRuns.reduce()
        val evaluated = evaluate(potentialDeadwood)
        evaluated to (potentialDeadwood to (newRuns + newSets))
    }.minByOrNull { it.first }!!.second
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
                .map { GRun(BitCardSet.of(it)) }
        }
        .flatten()
}

private fun findSets(cards: Collection<Card>): List<GSet> {
    return cards
        .groupBy { it.nominal }
        .filterValues { it.size >= 3 }
        .values
        .map { GSet(BitCardSet.of(it)) }
}

private fun Collection<CardSet>.reduce(): CardSet = if (isEmpty()) EmptyCardSet else reduce(CardSet::union)

fun evaluate(cards: CardSet): Int = cards.sumOf { evaluate(it) }

fun evaluate(card: Card): Int {
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