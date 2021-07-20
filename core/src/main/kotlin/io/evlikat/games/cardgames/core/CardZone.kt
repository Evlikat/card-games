package io.evlikat.games.cardgames.core

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