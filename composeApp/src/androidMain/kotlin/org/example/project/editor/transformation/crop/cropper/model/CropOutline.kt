package org.example.project.editor.transformation.crop.cropper.model

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import org.example.project.editor.transformation.crop.cropper.util.createPolygonShape

interface CropOutline {
    val id: Int
    val title: String
}

interface CropShape : CropOutline {
    val shape: Shape
}

interface CropPath : CropOutline {
    val path: Path
}

interface CropImageMask : CropOutline {
    val image: ImageBitmap
}

@Immutable
data class RectCropShape(
    override val id: Int,
    override val title: String,
) : CropShape {
    override val shape: Shape = RectangleShape
}

@Immutable
data class RoundedCornerCropShape(
    override val id: Int,
    override val title: String,
    val cornerRadius: CornerRadiusProperties = CornerRadiusProperties(),
    override val shape: RoundedCornerShape = RoundedCornerShape(
        topStartPercent = cornerRadius.topStartPercent,
        topEndPercent = cornerRadius.topEndPercent,
        bottomEndPercent = cornerRadius.bottomEndPercent,
        bottomStartPercent = cornerRadius.bottomStartPercent
    )
) : CropShape

@Immutable
data class CutCornerCropShape(
    override val id: Int,
    override val title: String,
    val cornerRadius: CornerRadiusProperties = CornerRadiusProperties(),
    override val shape: CutCornerShape = CutCornerShape(
        topStartPercent = cornerRadius.topStartPercent,
        topEndPercent = cornerRadius.topEndPercent,
        bottomEndPercent = cornerRadius.bottomEndPercent,
        bottomStartPercent = cornerRadius.bottomStartPercent
    )
) : CropShape

@Immutable
data class OvalCropShape(
    override val id: Int,
    override val title: String,
    val ovalProperties: OvalProperties = OvalProperties(),
    override val shape: Shape = CircleShape
) : CropShape

@Immutable
data class PolygonCropShape(
    override val id: Int,
    override val title: String,
    val polygonProperties: PolygonProperties = PolygonProperties(),
    override val shape: Shape = createPolygonShape(polygonProperties.sides, polygonProperties.angle)
) : CropShape

@Immutable
data class CustomPathOutline(
    override val id: Int,
    override val title: String,
    override val path: Path
) : CropPath

@Immutable
data class ImageMaskOutline(
    override val id: Int,
    override val title: String,
    override val image: ImageBitmap,
) : CropImageMask
