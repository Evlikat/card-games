package io.evlikat.games.cardgames.ginrummy.server

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.evlikat.games.cardgames.core.Card
import io.evlikat.games.cardgames.core.CardZones


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
    JsonSubTypes.Type(value = TellYesNo::class, name = "TellYesNo"),
    JsonSubTypes.Type(value = TellSelectCard::class, name = "TellSelectCard"),
    JsonSubTypes.Type(value = TellSelectZone::class, name = "TellSelectZone")
)
sealed class BaseTellMessage(val name: String) {
    lateinit var actor: String
}

class TellYesNo(val yes: Boolean) : BaseTellMessage("TellYesNo") {
    val no = !yes
}

class TellSelectZone(val zone: String) : BaseTellMessage("TellSelectZone")
class TellSelectCard(val card: String) : BaseTellMessage("TellSelectCard")

sealed class BaseServerMessage(val name: String)

class AskYesNo(val message: String) : BaseServerMessage("AskYesNo")
class AskSelectZone(val message: String, val zones: Collection<CardZones>) : BaseServerMessage("AskSelectZone")
class AskSelectCard(val message: String, val cards: Collection<Card>) : BaseServerMessage("AskSelectCard")

class CardMoved(val card: Card?, val from: CardZones, val to: CardZones) : BaseServerMessage("CardMoved")