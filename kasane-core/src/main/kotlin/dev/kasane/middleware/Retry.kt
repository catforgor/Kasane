package dev.kasane.middleware

import dev.kasane.core.Layer
import dev.kasane.core.Service
import kotlinx.coroutines.CancellationException

/**
 *
 * decides whether to retry from the outcome of an attempt (success or failure)
 *
 * @param attempt number of completed attempts
 *
 */
fun interface RetryPolicy<Req, Resp> {
    suspend fun shouldRetry(req: Req, attempt: Int, result: Result<Resp>): Boolean

    companion object {
        fun <Req, Resp> maxAttempts(maxAttempts: Int): RetryPolicy<Req, Resp> =
            RetryPolicy { _, attempt, result -> result.isFailure && attempt < maxAttempts }
    }
}

/**
 * Assumes [Req] is safe to invoke more than one, dont wrap around a service where 
 * something cant happen twice
 * 
 * TODO: fix above comment
 */
class RetryLayer<Req, Resp>(private val policy: RetryPolicy<Req, Resp>) : Layer<Req, Resp> {
    override fun wrap(inner: Service<Req, Resp>): Service<Req, Resp> = Service { req ->
        var attempt = 0
        while (true) {
            attempt++
            val result = try {
                Result.success(inner.invoke(req))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                Result.failure(e)
            }
            if (!policy.shouldRetry(req, attempt, result)) return@Service result.getOrThrow()
        }
        @Suppress("UNREACHABLE_CODE")
        throw IllegalStateException("unreachable")
    }
}
