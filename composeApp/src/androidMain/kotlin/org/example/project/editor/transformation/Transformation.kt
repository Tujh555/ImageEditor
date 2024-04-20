package tech.inno.dion.chat.image.editor.transformation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import org.example.project.editor.ComposeCanvas

@Stable
interface Transformation {

    context(DrawScope)
    fun drawOnCanvas(canvas: ComposeCanvas)

    @Composable
    fun Content(safeRect: Rect)
}
