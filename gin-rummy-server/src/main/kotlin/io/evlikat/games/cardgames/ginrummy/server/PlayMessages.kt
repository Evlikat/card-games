package io.evlikat.games.cardgames.ginrummy.server

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.evlikat.games.cardgames.core.Card
import io.evlikat.games.cardgames.core.CardZones


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "name")
@JsonSubTypes(
    JsonSubTypes.Type(value = TellYesNo::class, name = "TellYesNo"),
    JsonSubTypes.Type(value = TellSelectCard::class, name = "TellSelectCard"),
    JsonSubTypes.Type(value = TellSelectZone::class, name = "TellSelectZone")
)
sealed class BaseTellMessage(val name: String) {
    lateinit var actor: String
}

data class TellYesNo(val yes: Boolean) : BaseTellMessage("TellYesNo") {
    val no = !yes
}

data class TellSelectZone(val zone: String) : BaseTellMessage("TellSelectZone")
data class TellSelectCard(val card: String) : BaseTellMessage("TellSelectCard")

sealed class BaseServerMessage(val name: String)

data class AskYesNo(val message: String) : BaseServerMessage("AskYesNo")
data class AskSelectZone(val message: String, val zones: Collection<CardZones>) : BaseServerMessage("AskSelectZone")
data class AskSelectCard(val message: String, val cards: Collection<Card>) : BaseServerMessage("AskSelectCard")

data class CardMoved(val card: Card?, val from: CardZones, val to: CardZones) : BaseServerMessage("CardMoved")