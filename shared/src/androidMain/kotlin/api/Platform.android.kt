package api

import android.util.Log

actual fun log(message: Any) {
    Log.d("--tag", message.toString())
}