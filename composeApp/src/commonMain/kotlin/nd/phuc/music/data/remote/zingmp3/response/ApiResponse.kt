package nd.phuc.music.data.remote.zingmp3.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val err: Int? = null,
    val msg: String? = null,
    val data: T? = null,
    val timestamp: Long? = null,
)