package nd.phuc.youtube.models.body

import nd.phuc.youtube.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class GetTranscriptBody(
    val context: Context,
    val params: String,
)
