package org.example.project.editor.modifiers.capturable

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

interface DrawableCatcher {
    val captureRequests: SharedFlow<CaptureRequest>

    fun captureAsync(): Deferred<Bitmap>
}

class CaptureRequest(
    val imageBitmapDeferred: CompletableDeferred<Bitmap>,
)

fun DrawableCatcher(): DrawableCatcher = DrawableCatcherImpl()

private class DrawableCatcherImpl : DrawableCatcher {
    private val _captureRequests = MutableSharedFlow<CaptureRequest>(extraBufferCapacity = 1)
    override val captureRequests: SharedFlow<CaptureRequest> = _captureRequests

    override fun captureAsync(): Deferred<Bitmap> {
        val deferredImageBitmap = CompletableDeferred<Bitmap>()
        return deferredImageBitmap.also {
            _captureRequests.tryEmit(CaptureRequest(it))
        }
    }
}

