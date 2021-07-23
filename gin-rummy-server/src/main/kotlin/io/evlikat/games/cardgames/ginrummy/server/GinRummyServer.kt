package io.evlikat.games.cardgames.ginrummy.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication
class GinRummyServer

fun main(args: Array<String>) {
    SpringApplicationBuilder()
        .sources(GinRummyServer::class.java)
        .run(*args)
}