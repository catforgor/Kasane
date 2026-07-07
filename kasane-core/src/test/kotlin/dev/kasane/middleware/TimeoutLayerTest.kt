package dev.kasane.middleware

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.seconds

class TimeoutLayerTest {

    @Test
    fun `pass normally when inner finished on time`() = runTest {
        val service = TimeoutLayer<Unit, String>(5.seconds).wrap {
            delay(1.seconds)
            "ok"
        }

        assertEquals("ok", service.invoke(Unit))
    }

    @Test
    fun `throw TimeoutExceededException when inner doesnt finish on time`() = runTest {
        val service = TimeoutLayer<Unit, String>(1.seconds).wrap {
            delay(10.seconds)
            "too late"
        }

        assertFailsWith<TimeoutExceededException> { service.invoke(Unit) }
    }
}
