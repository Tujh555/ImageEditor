package org.example.project.editor.transformation.crop

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.example.project.R
import org.example.project.editor.transformation.Transformation

internal class CropTransformation(private val imagePath: Uri) : Transformation {
    override fun save(): Bitmap {
        TODO("Not yet implemented")
    }

    override fun undo() {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    @Composable
    override fun Content() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        val bounds = layoutCoordinates.boundsInParent()
//                        if (drawRect != bounds) {
//                            drawRect = bounds
//                        }
                    },
                model = ImageRequest
                    .Builder(LocalContext.current)
                    .data(imagePath)
                    .build(),
                contentDescription = null
            )
        }
    }

    @Composable
    override fun Controls() = Unit

}