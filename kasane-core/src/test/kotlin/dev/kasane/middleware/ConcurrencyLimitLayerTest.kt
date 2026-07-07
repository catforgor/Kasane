package dev.kasane.middleware

import dev.kasane.core.Service
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class ConcurrencyLimitLayerTest {

    @Test
    fun `never exceeds the given concurrency`() = runTest {
        val current = AtomicInteger(0)
        val observedMax = AtomicInteger(0)

        val service = ConcurrencyLimitLayer<Int, Unit>(maxConcurrent = 3).wrap {
            val now = current.incrementAndGet()
            observedMax.updateAndGet { max -> maxOf(max, now) }
            delay(1.seconds)
            current.decrementAndGet()
        }

        val jobs = (1..10).map { i -> launch { service.invoke(i) } }
        jobs.joinAll()

        assertTrue(observedMax.get() <= 3, "concurrency ${observedMax.get()} exceeded limit of 3")
    }
}
