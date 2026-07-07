package dev.kasane.middleware

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RetryLayerTest {

    @Test
    fun `retries until give up and throws the last error`() = runTest {
        var calls = 0
        val service = RetryLayer<Unit, String>(RetryPolicy.maxAttempts(3)).wrap {
            calls++
            error("calls: $calls")
        }

        val thrown = assertFailsWith<IllegalStateException> { service.invoke(Unit) }
        assertEquals("calls: 3", thrown.message)
        assertEquals(3, calls)
    }

    @Test
    fun `succeeds when inner stops failing`() = runTest {
        var calls = 0
        val service = RetryLayer<Unit, String>(RetryPolicy.maxAttempts(5)).wrap {
            calls++
            if (calls < 3) error("calls: $calls") else "ok"
        }

        assertEquals("ok", service.invoke(Unit))
        assertEquals(3, calls)
    }
}
