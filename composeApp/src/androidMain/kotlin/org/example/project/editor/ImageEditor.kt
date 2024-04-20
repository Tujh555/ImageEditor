package org.example.project.editor

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Stable
internal class ImageEditor(imageUri: Uri) {
    private val _bitmap = MutableStateFlow(BitmapFactory.decodeFile(imageUri.path))
    val bitmap = _bitmap.asStateFlow()

    @Composable
    fun Content() {
        val image by _bitmap.collectAsState()

        EditorLayout(
            modifier = Modifier.fillMaxSize(),
            image = image,
            onSave = { newBitmap ->
                _bitmap.update {
                    newBitmap
                }
            }
        )
    }
}