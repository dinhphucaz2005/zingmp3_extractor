package nd.phuc.music.platform

import android.util.Log

actual fun debugLog(message: String, tag: String?) {
    Log.d(tag, message)
}