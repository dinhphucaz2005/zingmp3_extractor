package nd.phuc.youtube.models.body

import nd.phuc.youtube.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class GetSearchSuggestionsBody(
    val context: Context,
    val input: String,
)
