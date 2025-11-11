package nd.phuc.youtube.utils

import io.ktor.utils.io.core.toByteArray
import java.security.MessageDigest

actual fun sha1(str: String): String = MessageDigest.getInstance("SHA-1").digest(str.toByteArray()).toHex()