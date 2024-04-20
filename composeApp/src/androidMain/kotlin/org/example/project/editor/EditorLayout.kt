package org.example.project.editor

import android.graphics.Bitmap
import android.graphics.Picture
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import org.example.project.editor.transformation.draw.DrawTransformation

@Composable
internal fun EditorLayout(
    modifier: Modifier = Modifier,
    image: Bitmap,
    onSave: (Bitmap) -> Unit
) {
    val currentTransformation = remember {
        DrawTransformation()
    }
    val composeBitmap = remember {
        image.asImageBitmap()
    }

    val picture = remember {
        Picture()
    }
    val drawRect = remember {
        mutableStateOf(Rect.Zero)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(width = 1.dp, color = Color.Green)
                .drawWithCache {
                    val bitmapPaint = Paint()

                    val imageOffset = Offset(
                        x = ((size.width - image.width) / 2f).coerceAtLeast(0f),
                        y = ((size.height - image.height) / 2f).coerceAtLeast(0f)
                    )

                    drawRect.value = Rect(imageOffset, Size(image.width.toFloat(), image.height.toFloat()))

                    onDrawWithContent {
                        val pictureCanvas = picture
                            .beginRecording(image.width, image.height)
                            .asCompose()

                        pictureCanvas.drawImage(
                            image = composeBitmap,
                            topLeftOffset = imageOffset,
                            paint = bitmapPaint
                        )

                        currentTransformation.drawOnCanvas(pictureCanvas)

                        picture.endRecording()

                        drawIntoCanvas { canvas ->
                            canvas.nativeCanvas.drawPicture(picture)
                        }
                    }
                }
        )

        currentTransformation.Content(drawRect.value)

        FloatingActionButton(
            modifier = Modifier
                .padding(end = 20.dp, bottom = 20.dp)
                .align(Alignment.BottomEnd),
            onClick = {
                val bitmap = Bitmap.createBitmap(
                    picture.width,
                    picture.height,
                    Bitmap.Config.ARGB_8888
                )
                val androidCanvas = AndroidCanvas(bitmap)
                androidCanvas.drawPicture(picture)
                onSave(bitmap)
            }
        ) {
            Text(text = " Save ")
        }
    }
}