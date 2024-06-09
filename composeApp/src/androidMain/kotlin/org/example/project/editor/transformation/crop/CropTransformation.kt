package org.example.project.editor.transformation.crop

import android.graphics.Bitmap
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import org.example.project.editor.transformation.CropData
import org.example.project.editor.transformation.Transformation
import org.example.project.editor.transformation.crop.cropper.ImageCropper
import org.example.project.editor.transformation.crop.cropper.crop.CropAgent
import org.example.project.editor.transformation.crop.cropper.model.OutlineType
import org.example.project.editor.transformation.crop.cropper.model.RectCropShape
import org.example.project.editor.transformation.crop.cropper.settings.CropDefaults
import org.example.project.editor.transformation.crop.cropper.settings.CropOutlineProperty
import org.example.project.editor.transformation.crop.cropper.state.CropState

internal class CropTransformation(private val initialBitmap: ImageBitmap) : Transformation {
    private val actionChannel = Channel<suspend () -> Unit>(Channel.UNLIMITED)
    private var cropState: CropState? = null
    private var cropData: CropData? = null
    private val cropAgent = CropAgent()
    private var currentDensity: Density? = null

    override fun save(): Bitmap {
        val data = cropData ?: return initialBitmap.asAndroidBitmap()
        val density = currentDensity ?: return initialBitmap.asAndroidBitmap()

        val croppedImageBitmap = cropAgent.crop(
            imageBitmap = data.scaledImageBitmap,
            cropRect = data.cropRect,
            cropOutline = data.cropOutline,
            layoutDirection = LayoutDirection.Ltr,
            density = density
        )
        val resizedBitmap = if (data.requiredSize != null) {
            cropAgent.resize(
                croppedImageBitmap,
                data.requiredSize.width,
                data.requiredSize.height,
            )
        } else {
            croppedImageBitmap
        }
        return resizedBitmap.asAndroidBitmap()
    }

    override fun undo() {
        actionChannel.trySend {
            cropState?.animateOverlayRectTo()
            cropState?.resetWithAnimation()
        }
    }

    override fun clear() = undo()

    @Composable
    override fun Content() {
        val handleSize: Float = LocalDensity.current.run { 20.dp.toPx() }

        val cropProperties = remember {
            CropDefaults.properties(
                cropOutlineProperty = CropOutlineProperty(
                    OutlineType.Rect,
                    RectCropShape(0, "Rect")
                ),
                handleSize = handleSize
            )
        }
        val backgroundColor = MaterialTheme.colorScheme.onBackground

        ImageCropper(
            imageBitmap = initialBitmap,
            contentDescription = null,
            cropProperties = cropProperties,
            cropStyle = remember(backgroundColor) {
                CropDefaults.style(backgroundColor = backgroundColor)
            },
            onCropStateChanged = {
                cropState = it
            },
            onCropDataChanged = {
                cropData = it
            }
        )


        LaunchedEffect(Unit) {
            for (action in actionChannel) {
                action()
            }
        }

        val density = LocalDensity.current

        LaunchedEffect(density) {
            currentDensity = density
        }
    }

    @Composable
    override fun Controls() = Unit
}