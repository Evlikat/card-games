package io.evlikat.games.cardgames.core

enum class SuitColor {
    RED,
    BLACK
}

enum class Suit(val sign: String, val color: SuitColor) {
    DIAMONDS("♦", SuitColor.RED),
    HEARTS("♥", SuitColor.RED),
    CLUBS("♣", SuitColor.BLACK),
    SPADES("♠", SuitColor.BLACK)
}

enum class Nominal(val sign: String) {
    A("A"),
    C2("2"),
    C3("3"),
    C4("4"),
    C5("5"),
    C6("6"),
    C7("7"),
    C8("8"),
    C9("9"),
    C10("10"),
    J("J"),
    Q("Q"),
    K("K")
}

data class Card(val suit: Suit, val nominal: Nominal) {
    infix fun sameSuitAs(card: Card) = suit == card.suit

    override fun toString(): String = "${suit.sign}${nominal.sign}"
    fun precedes(card: Card): Boolean = nominal.ordinal + 1 == card.nominal.ordinal
}

infix fun Int.of(suit: Suit): Card = Card(suit, Nominal.values()[this - 1])
infix fun Char.of(suit: Suit): Card = Card(
    suit, when (this.toLowerCase()) {
        'a' -> Nominal.A
        'q' -> Nominal.Q
        'k' -> Nominal.K
        'j' -> Nominal.J
        else -> throw IllegalArgumentException("Unrecognized character")
    }
)

class Watcher {
    fun cardMoved(card: Card, from: CardZone, to: CardZone) {

    }
}

class Deck private constructor(private val cards: MutableList<Card>) : CardZone {

    override lateinit var watcher: Watcher

    companion object {
        fun standard52(): Deck =
            Deck(Suit.values().flatMapTo(mutableListOf()) { s -> Nominal.values().map { Card(s, it) } })
    }

    fun shuffle() = cards.shuffle()

    override fun draw(): Card {
        return cards.removeAt(cards.lastIndex)
    }

    override fun draw(card: Card): Card {
        if (!cards.remove(card)) {
            throw IllegalStateException("The $card is not in hand")
        }
        return card
    }

    override fun putOnTop(card: Card) {
        cards.add(card)
    }
}

class Discard(private val cards: MutableList<Card> = mutableListOf()) : CardZone {

    override lateinit var watcher: Watcher

    override fun draw(): Card {
        return cards.removeAt(cards.lastIndex)
    }

    override fun draw(card: Card): Card {
        if (!cards.remove(card)) {
            throw IllegalStateException("The $card is not in hand")
        }
        return card
    }

    override fun putOnTop(card: Card) {
        cards.add(card)
    }
}

interface CardOrder : Comparator<Card>

interface CardZone {

    val watcher: Watcher

    fun draw(): Card
    fun draw(card: Card): Card
    fun putOnTop(card: Card)

    fun moveCardTo(zone: CardZone) {
        val card = this.draw()
        watcher.cardMoved(card = card, from = this, to = zone)
        zone.putOnTop(card)
    }

    fun moveCardTo(card: Card, zone: CardZone) {
        this.draw(card)
        watcher.cardMoved(card = card, from = this, to = zone)
        zone.putOnTop(card)
    }
}

class Player(
    val name: String
) {
    fun askYesNo(message: String): Boolean {
        TODO("Not yet implemented")
    }

    fun askSelectZone(message: String, vararg cardZones: CardZone): CardZone {
        TODO("Not yet implemented")
    }

    fun askSelectCard(message: String, cards: Collection<Card>): Card {
        TODO("Not yet implemented")
    }
}

class Hand(val player: Player) : CardZone {

    override lateinit var watcher: Watcher
    private val cards: MutableList<Card> = mutableListOf()

    val allCards: List<Card> = cards

    override fun draw(): Card {
        TODO("Not drawable zone")
    }

    override fun draw(card: Card): Card {
        if (!cards.remove(card)) {
            throw IllegalStateException("The $card is not in hand")
        }
        return card
    }

    override fun putOnTop(card: Card) {
        cards.add(card)
    }

}