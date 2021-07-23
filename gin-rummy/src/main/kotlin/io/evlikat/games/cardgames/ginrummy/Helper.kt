package io.evlikat.games.cardgames.ginrummy

import io.evlikat.games.cardgames.core.BitCardSet
import io.evlikat.games.cardgames.core.Card
import io.evlikat.games.cardgames.core.CardSet
import io.evlikat.games.cardgames.core.EmptyCardSet
import io.evlikat.games.cardgames.core.Nominal.*

fun findCombinations(cards: CardSet): Pair<CardSet, List<Combination>> {
    val allCards = BitCardSet.of(cards)

    val runs = findRunCandidates(cards, minSize = 3).map { GRun(it) }
    val sets = findSetCandidates(cards, minSize = 3).map { GSet(it) }

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

    val combinations = findCombinationsRecursive(
        intersection = contestedCards,
        pointMap = points,
        distKey = mapOf()
    )
    val optimalCombination = combinations.minByOrNull { evaluate((cards - it.values.reduce()).toCardSet()) }!!

    val deadwood = (cards - optimalCombination.values.reduce()).toCardSet()
    return deadwood to optimalCombination.values.distinct()
}

private fun findCombinationsRecursive(
    intersection: CardSet,
    pointMap: Map<Card, Pair<GRun, GSet>>,
    distKey: Map<Card, Combination>
): List<Map<Card, Combination>> {
    if (intersection.isEmpty()) {
        return listOf(distKey)
    }
    val contestedPoint = intersection.first()
    val (run, set) = pointMap.getValue(contestedPoint)
    return findCombinationsRecursive(
        intersection = intersection - contestedPoint,
        pointMap = pointMap,
        distKey = distKey + (contestedPoint to set)
    ) + findCombinationsRecursive(
        intersection = intersection - contestedPoint,
        pointMap = pointMap,
        distKey = distKey + (contestedPoint to run)
    )
}

fun completeCombinations(
    combinations: List<Combination>,
    additionalCardSet: CardSet
): Pair<List<Combination>, CardSet> {
    val runCandidates = findRunCandidates(additionalCardSet, minSize = 2)
    val runByStarts = combinations.filterIsInstance<GRun>().associateBy { it.firstCard }
    val runsByEnds = combinations.filterIsInstance<GRun>().associateBy { it.lastCard }
    val setsByNominal = combinations.filterIsInstance<GSet>().associateBy { it.nominal }

    val setCompletionCandidates = additionalCardSet
        .mapNotNull { c -> setsByNominal[c.nominal]?.let { it to c } }
        .toMap()
    val runCompletionCandidates = (additionalCardSet - runCandidates.reduce())
        .mapNotNull { c ->
            val toRun = (c.nextInSuitOrNull()?.let { runByStarts[it] } ?: c.prevInSuitOrNull()?.let { runsByEnds[it] })
            toRun?.let { it to c }
        }
        .toMap()
    val runCompletionRunCandidates = runCandidates
        .mapNotNull { c ->
            val toRun = (c.last().nextInSuitOrNull()?.let { runByStarts[it] }
                ?: c.first().prevInSuitOrNull()?.let { runsByEnds[it] })
            toRun?.let { it to c }
        }
        .toMap()

    val intersection =
        setCompletionCandidates.values intersect (runCompletionRunCandidates.values.reduce() + runCompletionCandidates.values)
    if (intersection.isEmpty()) {
        val newCombinations = combinations.map { combination ->
            setCompletionCandidates[combination]?.let { (combination + it) as Combination }
                ?: runCompletionCandidates[combination]?.let { (combination + it) as Combination }
                ?: runCompletionRunCandidates[combination]?.let { (combination + it) as Combination }
                ?: combination
        }
        val newDeadwood = additionalCardSet - setCompletionCandidates.values.toCardSet() -
                runCompletionRunCandidates.values.reduce() - runCompletionCandidates.values.toCardSet()
        return newCombinations to newDeadwood
    }
    val combinationCompletionOptions = completeCombinationsRecursive(
        intersection = intersection,
        setCompletionCandidates = setCompletionCandidates.map { it.value to it.key }.toMap(),
        runCompletionCandidates = runCompletionCandidates.map { it.value to it.key }.toMap(),
        runCompletionRunCandidates = runCompletionRunCandidates.map { it.value to it.key }.toMap(),
        distKey = mapOf()
    )
    val optimalCombinationCompletion =
        combinationCompletionOptions.minByOrNull { evaluate(additionalCardSet - it.keys.reduce()) }!!
    val newDeadwood = additionalCardSet - optimalCombinationCompletion.keys.reduce()
    val optimalCombinationCompletionReversed = optimalCombinationCompletion.map { it.value to it.key }.toMap()
    val newCombinations = combinations.map { combination ->
        optimalCombinationCompletionReversed[combination]?.let { (combination + it) as Combination } ?: combination
    }
    return newCombinations to newDeadwood
}

private fun completeCombinationsRecursive(
    intersection: Set<Card>,
    setCompletionCandidates: Map<Card, GSet>,
    runCompletionCandidates: Map<Card, GRun>,
    runCompletionRunCandidates: Map<CardSet, GRun>,
    distKey: Map<CardSet, Combination>
): List<Map<CardSet, Combination>> {
    if (intersection.isEmpty()) {
        return listOf(distKey)
    }
    val contestedPoint = intersection.first()
    val gSet = setCompletionCandidates.getValue(contestedPoint)
    val (gRun, bindCards) = (runCompletionCandidates[contestedPoint]?.let { it to EmptyCardSet }
        ?: runCompletionRunCandidates.entries.find { contestedPoint in it.key }?.let { it.value to it.key })!!

    return completeCombinationsRecursive(
        intersection = intersection - contestedPoint,
        setCompletionCandidates = setCompletionCandidates,
        runCompletionCandidates = runCompletionCandidates,
        runCompletionRunCandidates = runCompletionRunCandidates,
        distKey = distKey + (BitCardSet.of(contestedPoint) to gSet)
    ) + if (bindCards.isEmpty()) completeCombinationsRecursive(
        intersection = intersection - contestedPoint,
        setCompletionCandidates = setCompletionCandidates,
        runCompletionCandidates = runCompletionCandidates,
        runCompletionRunCandidates = runCompletionRunCandidates,
        distKey = distKey + (BitCardSet.of(contestedPoint) to gRun)
    ) else completeCombinationsRecursive(
        intersection = intersection - contestedPoint,
        setCompletionCandidates = setCompletionCandidates,
        runCompletionCandidates = runCompletionCandidates,
        runCompletionRunCandidates = runCompletionRunCandidates,
        distKey = distKey + (bindCards to gRun)
    )
}

private fun findRunCandidates(cards: Collection<Card>, minSize: Int): List<CardSet> {
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
                .filter { it.size >= minSize }
                .map { BitCardSet.of(it) }
        }
        .flatten()
}

private fun findSetCandidates(cards: Collection<Card>, minSize: Int): List<CardSet> {
    return cards
        .groupBy { it.nominal }
        .filterValues { it.size >= minSize }
        .values
        .map { BitCardSet.of(it) }
}

private fun Collection<Card>.toCardSet(): CardSet = BitCardSet.of(this)
private fun Collection<CardSet>.reduce(): CardSet = if (isEmpty()) EmptyCardSet else reduce(CardSet::union)

fun evaluate(cards: CardSet): Int = cards.sumOf { evaluate(it) }

fun evaluate(card: Card): Int {
    return when (card.nominal) {
        C10, J, Q, K -> 10
        else -> card.nominal.ordinal + 1
    }
}

private fun Card.prevInSuitOrNull(): Card? {
    if (ordinal == 0) {
        return null
    }
    val candidate = Card.values()[ordinal - 1]
    if (candidate.suit != suit) {
        return null
    }
    return candidate
}

private fun Card.nextInSuitOrNull(): Card? {
    if (ordinal == values().lastIndex) {
        return null
    }
    val candidate = Card.values()[ordinal + 1]
    if (candidate.suit != suit) {
        return null
    }
    return candidate
}