package nd.phuc.music.data.remote.zingmp3

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import nd.phuc.music.env.BuildConfig
import nd.phuc.music.data.remote.zingmp3.response.ApiResponse
import nd.phuc.music.data.remote.zingmp3.response.SongStreaming
import java.security.MessageDigest
import java.util.zip.GZIPInputStream
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object ZingMp3Client {

    val client = HttpClient {
        expectSuccess = true
        defaultRequest {
            url {
                takeFrom("https://zingmp3.vn/")
            }
            headers {
                append("Accept", "application/json, text/javascript, */*; q=0.01")
                append("Accept-Encoding", "gzip, deflate, br, zstd")
                append("Accept-Language", "en-US,en;q=0.5")
                append("Connection", "keep-alive")
                append("Origin", "https://zingmp3.vn")
                append("Referer", "https://zingmp3.vn/")
                append("Session-Key", "")
                append("Api-Key", "X5BM3w8N7MKozC0B85o4KMlzLZKhV00y")
                append("Segment", "m")
                append("cookie", BuildConfig.COOKIES)
                append(
                    "User-Agent",
                    "Mozilla/5.0 (X11; Linux x86_64; rv:144.0) Gecko/20100101 Firefox/144.0"
                )
            }
        }
        expectSuccess = true
        install(HttpTimeout) {
            val timeout = 30000L
            connectTimeoutMillis = timeout
            requestTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
            logger = object : Logger {
                override fun log(message: String) = println(message)
            }
        }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    enum class SignatureType { SONG_STREAMING }

    private fun createSig(id: String, signatureType: SignatureType): String {
        val ctime = System.currentTimeMillis() / 1000
        val version = "1.17.2"
        val e = "ctime=$ctime" + "id=$id" + "version=$version"
        val sha256Hex = MessageDigest.getInstance("SHA-256")
            .digest(e.toByteArray())
            .joinToString("") { "%02x".format(it) }

        val t = when (signatureType) {
            SignatureType.SONG_STREAMING -> "/api/v2/song/get/streaming"
        } + sha256Hex

        val key = when (signatureType) {
            SignatureType.SONG_STREAMING -> "acOrvUS15XRW2o9JksiK1KgQ6Vbds8ZW"
        }

        val hmacBytes = Mac.getInstance("HmacSHA512").apply {
            init(SecretKeySpec(key.toByteArray(), "HmacSHA512"))
        }.doFinal(t.toByteArray())

        return hmacBytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun getSongStreaming(id: String): ApiResponse<SongStreaming> {
        val responseBytes: ByteArray = client.get("/api/v2/song/get/streaming") {
            addSignatureParams(id = id, signatureType = SignatureType.SONG_STREAMING)
        }.body()

        val decompressed = GZIPInputStream(responseBytes.inputStream()).readBytes()
        val jsonString = decompressed.decodeToString()

        val json = Json { ignoreUnknownKeys = true }
        val serializer = ApiResponse.serializer(SongStreaming.serializer())
        return json.decodeFromString(serializer, jsonString)

        return Json { ignoreUnknownKeys = true }
            .decodeFromString(
                ApiResponse.serializer(
                    typeSerial0 = SongStreaming.serializer()
                ), jsonString
            )
        return client.get("/api/v2/song/get/streaming") {
            addSignatureParams(id, SignatureType.SONG_STREAMING)
        }.body()
    }

    private fun HttpRequestBuilder.addSignatureParams(
        id: String,
        signatureType: SignatureType,
    ) {
        val ctime = System.currentTimeMillis() / 1000
        val version = "1.17.2"
        val sig = createSig(id, signatureType)

        url.parameters.append("id", id)
        url.parameters.append("ctime", ctime.toString())
        url.parameters.append("version", version)
        url.parameters.append("sig", sig)
        url.parameters.append("apiKey", "X5BM3w8N7MKozC0B85o4KMlzLZKhV00y")
    }
}
