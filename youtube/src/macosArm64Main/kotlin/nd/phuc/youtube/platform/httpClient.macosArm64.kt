package nd.phuc.youtube.platform

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(markerClass = [ExperimentalSerializationApi::class])
actual fun createClient(block: HttpClientConfig<*>.() -> Unit): HttpClient = HttpClient(Darwin) {
    block()
}