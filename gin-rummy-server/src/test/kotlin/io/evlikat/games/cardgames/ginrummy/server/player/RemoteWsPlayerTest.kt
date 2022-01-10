package io.evlikat.games.cardgames.ginrummy.server.player

import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.concurrent.TimeUnit.SECONDS

internal class RemoteWsPlayerTest {

    @Test
    @Timeout(1L, unit = SECONDS)
    internal fun `should accept yes`() {
        val messageSender: MessageSender = mock()
        val player = RemoteWsPlayer("p1", "c1", messageSender)

        whenever(messageSender.send(any())).then {
            player.resolveYesNo(yes = true)
        }
        val result = runBlocking {
            player.askYesNo("Yes?")
        }

        assertTrue(result)
    }
}