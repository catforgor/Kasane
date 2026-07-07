package dev.kasane.core

// One async [Req] -> [Resp]
fun interface Service<in Req, out Resp> {
    suspend fun invoke(req: Req): Resp
}
