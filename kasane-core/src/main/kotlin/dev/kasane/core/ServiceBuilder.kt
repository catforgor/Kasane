package dev.kasane.core

class ServiceBuilder<Req, Resp> {
    private val layers = mutableListOf<Layer<Req, Resp>>()

    fun layer(layer: Layer<Req, Resp>): ServiceBuilder<Req, Resp> {
        layers.add(layer)
        return this
    }

    fun service(inner: Service<Req, Resp>): Service<Req, Resp> =
        layers.foldRight(inner) { layer, acc -> layer.wrap(acc) }
}
