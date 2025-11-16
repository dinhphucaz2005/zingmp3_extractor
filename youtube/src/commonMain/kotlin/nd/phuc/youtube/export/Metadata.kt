package nd.phuc.youtube.export

import kotlinx.serialization.Serializable

@Serializable
data class Metadata(
    val id: String,
    val title: String,
    val thumbnail: String,
)