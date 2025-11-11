package nd.phuc.youtube.export

import kotlinx.serialization.Serializable

@Serializable
data class SearchSummaryResult(
    val id: String?,
    val title: String?,
    var cover: String?,
)