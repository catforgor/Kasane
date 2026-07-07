package dev.kasane.core

class ServiceBuilder<Req, Resp> private constructor(
    private val layers: List<Layer<Req, Resp>>,
) {
    constructor() : this(emptyList())

    fun layer(layer: Layer<Req, Resp>): ServiceBuilder<Req, Resp> =
        ServiceBuilder(layers + layer)

    fun service(inner: Service<Req, Resp>): Service<Req, Resp> =
        layers.foldRight(inner) { layer, acc -> layer.wrap(acc) }
}
