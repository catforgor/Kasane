package dev.kasane.core

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ServiceBuilderTest {

    // records name for asserting wrap order
    private fun tracing(name: String, trace: MutableList<String>): Layer<String, String> =
        Layer { inner ->
            Service { req ->
                trace += "$name:enter"
                val result = inner.invoke(req)
                trace += "$name:exit"
                result
            }
        }

    @Test
    fun `layer wraps the inner service`() = runTest {
        val trace = mutableListOf<String>()
        val service = ServiceBuilder<String, String>()
            .layer(tracing("A", trace))
            .service(Service { req -> "$req-handled" })

        val result = service.invoke("req")

        assertEquals("req-handled", result)
        assertEquals(listOf("A:enter", "A:exit"), trace)
    }
}
