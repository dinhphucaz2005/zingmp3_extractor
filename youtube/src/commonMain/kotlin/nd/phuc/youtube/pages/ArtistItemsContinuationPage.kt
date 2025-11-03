package nd.phuc.youtube.pages

import nd.phuc.youtube.models.YTItem

data class ArtistItemsContinuationPage(
    val items: List<YTItem>,
    val continuation: String?,
)
