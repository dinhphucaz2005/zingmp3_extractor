package nd.phuc.youtube.platform

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.winhttp.WinHttp
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(markerClass = [ExperimentalSerializationApi::class])
actual fun createClient(block: HttpClientConfig<*>.() -> Unit): HttpClient = HttpClient(WinHttp) {
    block()
}