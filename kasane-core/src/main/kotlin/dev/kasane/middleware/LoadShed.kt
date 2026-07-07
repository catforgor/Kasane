package dev.kasane.middleware

import dev.kasane.core.Layer
import dev.kasane.core.Service
import kotlinx.coroutines.sync.Semaphore

class Overloaded : Exception("Service overloaded, request shed")

/**
 *
 * Bounds number of in flight requests through the wrapped service
 *
 * Rejects callers past limit, use [ConcurrencyLimitLayer] for queueing instead
 *
 */
class LoadShedLayer<Req, Resp>(private val maxConcurrent: Int) : Layer<Req, Resp> {
    override fun wrap(inner: Service<Req, Resp>): Service<Req, Resp> {
        val semaphore = Semaphore(maxConcurrent)
        return Service { req ->
            if (!semaphore.tryAcquire()) throw Overloaded()
            try {
                inner.invoke(req)
            } finally {
                semaphore.release()
            }
        }
    }
}
