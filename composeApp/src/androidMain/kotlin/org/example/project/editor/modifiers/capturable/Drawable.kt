package org.example.project.editor.modifiers.capturable

import android.graphics.Bitmap
import android.graphics.Picture
import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawModifierNode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun Modifier.drawable(
    controller: DrawableCatcher
): Modifier {
    return this then DrawableModifierNodeElement(controller)
}

private data class DrawableModifierNodeElement(
    private val catcher: DrawableCatcher,
) : ModifierNodeElement<DrawableModifierNode>() {
    override fun create(): DrawableModifierNode {
        return DrawableModifierNode(catcher)
    }

    override fun update(node: DrawableModifierNode) {
        node.update(catcher)
    }
}

@Suppress("unused")
private class DrawableModifierNode(
    controller: DrawableCatcher
) : DelegatingNode(), DelegatableNode {
    private val currentCatcher = MutableStateFlow(controller)

    override fun onAttach() {
        super.onAttach()
        coroutineScope.launch {
            observeCaptureRequestsAndServe()
        }
    }

    fun update(newCatcher: DrawableCatcher) {
        currentCatcher.value = newCatcher
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun observeCaptureRequestsAndServe() {
        currentCatcher
            .flatMapLatest { it.captureRequests }
            .collect { request ->
                val completable = request.imageBitmapDeferred
                try {
                    val picture = getCurrentContentAsPicture()
                    val bitmap = withContext(Dispatchers.Default) {
                        picture.asBitmap(Bitmap.Config.ARGB_8888)
                    }
                    completable.complete(bitmap)
                } catch (error: Throwable) {
                    completable.completeExceptionally(error)
                }
            }
    }

    private suspend fun getCurrentContentAsPicture(): Picture {
        return Picture().apply { drawCanvasIntoPicture(this) }
    }

    private suspend fun drawCanvasIntoPicture(picture: Picture) {
        val pictureDrawn = CompletableDeferred<Unit>()

        val delegatedNode = delegate(
            CacheDrawModifierNode {
                val width = this.size.width.toInt()
                val height = this.size.height.toInt()

                onDrawWithContent {
                    val pictureCanvas = Canvas(picture.beginRecording(width, height))

                    draw(this, this.layoutDirection, pictureCanvas, this.size) {
                        this@onDrawWithContent.drawContent()
                    }

                    picture.endRecording()

                    drawIntoCanvas { canvas ->
                        canvas.nativeCanvas.drawPicture(picture)

                        pictureDrawn.complete(Unit)
                    }
                }
            }
        )

        pictureDrawn.await()
        undelegate(delegatedNode)
    }

    private fun Picture.asBitmap(config: Bitmap.Config): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Bitmap.createBitmap(this@asBitmap)
        } else {
            val bitmap = Bitmap.createBitmap(width, height, config)
            val canvas = android.graphics.Canvas(bitmap)
            canvas.drawColor(android.graphics.Color.WHITE)
            canvas.drawPicture(this@asBitmap)
            bitmap
        }
    }
}