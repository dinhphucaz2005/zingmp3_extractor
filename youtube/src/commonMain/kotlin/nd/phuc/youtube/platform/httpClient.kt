package nd.phuc.youtube.platform

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
expect fun createClient(
    block: HttpClientConfig<*>.() -> Unit,
): HttpClient