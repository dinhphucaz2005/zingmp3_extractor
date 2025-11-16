package nd.phuc.youtube.export

import nd.phuc.youtube.YouTube

suspend fun search(
    query: String,
) {

    val result = YouTube.search(
        query = query,
        filter = YouTube.SearchFilter.FILTER_VIDEO,
    ).getOrNull() ?: throw Exception("Failed to get search results")

    result.items.forEach {
        println(
            createMetadata(
                ytItem = it,
                type = null,
                prefix = "search/${query.sanitizeFileName()}",
            )
        )
    }
}

