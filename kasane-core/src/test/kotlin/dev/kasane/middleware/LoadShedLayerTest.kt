package dev.kasane.middleware

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class LoadShedLayerTest {

    @Test
    fun `rejects once the concurrency limit is already in flight`() = runTest {
        val release = CompletableDeferred<Unit>()
        val entered = CompletableDeferred<Unit>()

        val service = LoadShedLayer<Unit, Unit>(maxConcurrent = 1).wrap {
            entered.complete(Unit)
            release.await()
        }

        val holder = launch { service.invoke(Unit) }
        entered.await()

        assertFailsWith<Overloaded> { service.invoke(Unit) }

        release.complete(Unit)
        holder.join()
    }
}
