package io.evlikat.games.cardgames.ginrummy.server.player

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.mockito.kotlin.mock
import java.util.concurrent.TimeUnit.SECONDS

internal class RemoteWsPlayerTest {

    @Test
    @Timeout(1L, unit = SECONDS)
    internal fun `should accept yes`() {
        val messageSender: MessageSender = mock()
        val player = RemoteWsPlayer("p1", "c1", messageSender)

        var result = false
        runBlocking {
            launch {
                result = player.askYesNo("Yes?")
            }
            launch {
                player.resolveYesNo(yes = true)
            }
        }

        assertTrue(result)
    }
}