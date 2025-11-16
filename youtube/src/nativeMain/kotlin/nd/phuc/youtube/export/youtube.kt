@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package nd.phuc.youtube.export

import kotlinx.cinterop.*
import kotlin.native.CName
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import nd.phuc.youtube.YouTube
import nd.phuc.youtube.export.SearchSummaryResult
import kotlin.experimental.ExperimentalNativeApi


/**
 * Trả về JSON string của danh sách [SearchSummaryResult]
 */
@Suppress("unused")
@CName("YouTube_searchSummary")
fun searchSummary(
    queryPtr: CPointer<ByteVar>?,
    visitorData: CPointer<ByteVar>?,
): CPointer<ByteVar>? {
    if (queryPtr == null) return null

    val query = queryPtr.toKString()

    val resultJson = runBlocking {
        visitorData?.let {
            YouTube.visitorData = it.toKString()
        }
        val result = YouTube.search(
            query = query,
            filter = YouTube.SearchFilter.FILTER_VIDEO,
        ).getOrNull() ?: return@runBlocking null

        val data = result.items.map {
            SearchSummaryResult(
                id = it.id,
                title = it.title,
                thumbnail = it.thumbnail,
            )
        }
        Json.encodeToString(data)
    }

    return resultJson?.let { json ->
        nativeHeap.allocArray<ByteVar>(json.length + 1).also { heapPtr ->
            for (i in json.indices) {
                heapPtr[i] = json[i].code.toByte()
            }
            heapPtr[json.length] = 0
        }
    }
}


@Suppress("unused")
@CName("YouTube_freeSearchSummary")
fun freeSearchSummary(ptr: CPointer<ByteVar>?) {
    if (ptr != null) nativeHeap.free(ptr)
}
