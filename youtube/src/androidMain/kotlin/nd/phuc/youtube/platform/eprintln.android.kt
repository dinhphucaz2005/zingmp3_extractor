package nd.phuc.youtube.platform

import android.util.Log

actual fun eprintln(tag: String?, message: String) {
    if (tag != null) {
        Log.e(tag, message)
    } else {
        Log.e("[]", message)
    }
}