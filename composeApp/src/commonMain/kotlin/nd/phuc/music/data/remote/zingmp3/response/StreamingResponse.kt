package nd.phuc.music.data.remote.zingmp3.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SongStreaming(
    @SerialName("128") val lowQuality: String? = null,
    @SerialName("320") val highQuality: String? = null,
)
