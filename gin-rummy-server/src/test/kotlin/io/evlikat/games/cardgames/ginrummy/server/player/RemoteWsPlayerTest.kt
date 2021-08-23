package io.evlikat.games.cardgames.ginrummy.server.player

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.mockito.kotlin.mock
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.SECONDS

internal class RemoteWsPlayerTest {

    private val pool = Executors.newFixedThreadPool(2)

    @Test
    @Timeout(1L, unit = SECONDS)
    internal fun `should accept yes`() {
        val messageSender: MessageSender = mock()
        val player = RemoteWsPlayer("p1", messageSender)

        val result = pool.submit(Callable {
            player.askYesNo("Yes?")
        })

        Thread.sleep(100)

        pool.submit {
            player.resolveYesNo(yes = true)
        }

        assertTrue(result.get())
    }
}