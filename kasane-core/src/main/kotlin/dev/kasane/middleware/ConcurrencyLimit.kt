package dev.kasane.middleware

import dev.kasane.core.Layer
import dev.kasane.core.Service
import dev.kasane.core.ServiceBuilder
import kotlinx.coroutines.sync.Semaphore

/**
 *
 * Bounds number on in flight requests through the wrapped service
 *
 * Queues callers past limit, use [LoadShedLayer] for rejecting instead
 *
 */
class ConcurrencyLimitLayer<Req, Resp>(private val maxConcurrent: Int) : Layer<Req, Resp> {
    override fun wrap(inner: Service<Req, Resp>): Service<Req, Resp> {
        val semaphore = Semaphore(maxConcurrent)
        return Service { req ->
            semaphore.acquire()
            try {
                inner.invoke(req)
            } finally {
                semaphore.release()
            }
        }
    }
}

fun <Req, Resp> ServiceBuilder<Req, Resp>.concurrencyLimit(maxConcurrent: Int): ServiceBuilder<Req, Resp> =
    layer(ConcurrencyLimitLayer(maxConcurrent))
