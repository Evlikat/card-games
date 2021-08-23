package io.evlikat.games.cardgames.ginrummy.server

import io.evlikat.games.cardgames.ginrummy.server.player.MessageSender
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import java.lang.Exception

class SimpMessageSender(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val clientId: String
) : MessageSender {

    private val log = LoggerFactory.getLogger(SimpMessageSender::class.java)

    override fun send(message: BaseServerMessage) {
        log.info("Sending $message")
        try {
            simpMessagingTemplate.convertAndSend("/client/${clientId}", message)
        } catch (ex: Exception) {
            log.warn("Could not send message", ex)
        }
    }
}