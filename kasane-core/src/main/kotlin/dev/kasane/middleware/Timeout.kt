package dev.kasane.middleware

import dev.kasane.core.Layer
import dev.kasane.core.Service
import dev.kasane.core.ServiceBuilder
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration

class TimeoutExceededException(val duration: Duration) : Exception("Timed out after $duration")

class TimeoutLayer<Req, Resp>(private val duration: Duration) : Layer<Req, Resp> {
    override fun wrap(inner: Service<Req, Resp>): Service<Req, Resp> = Service { req ->
        try {
            withTimeout(duration) { inner.invoke(req) }
        } catch (e: TimeoutCancellationException) {
            throw TimeoutExceededException(duration)
        }
    }
}

fun <Req, Resp> ServiceBuilder<Req, Resp>.timeout(duration: Duration): ServiceBuilder<Req, Resp> =
    layer(TimeoutLayer(duration))
