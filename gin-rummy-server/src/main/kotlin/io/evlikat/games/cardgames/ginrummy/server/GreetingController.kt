package io.evlikat.games.cardgames.ginrummy.server

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import org.springframework.web.util.HtmlUtils

@Controller
class GreetingController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    fun greeting(message: HelloMessage): Greeting {
        Thread.sleep(1000)
        return Greeting("Hello, " + HtmlUtils.htmlEscape(message.name) + "!")
    }
}

data class Greeting(val content: String)
data class HelloMessage(val name: String)