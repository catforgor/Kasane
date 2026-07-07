package dev.kasane.core

// Wraps a [Service] to make another [Service] of the same req/resp types
fun interface Layer<Req, Resp> {
    fun wrap(inner: Service<Req, Resp>): Service<Req, Resp>
}
