package org.example.project.editor.transformation.crop.cropper.model

interface CropOutlineContainer<O : CropOutline> {
    var selectedIndex: Int
    val outlines: List<O>
    val selectedItem: O
        get() = outlines[selectedIndex]
    val size: Int
        get() = outlines.size
}

data class RectOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<RectCropShape>
) : CropOutlineContainer<RectCropShape>

data class RoundedRectOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<RoundedCornerCropShape>
) : CropOutlineContainer<RoundedCornerCropShape>

data class CutCornerRectOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<CutCornerCropShape>
) : CropOutlineContainer<CutCornerCropShape>

data class OvalOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<OvalCropShape>
) : CropOutlineContainer<OvalCropShape>

data class PolygonOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<PolygonCropShape>
) : CropOutlineContainer<PolygonCropShape>

data class CustomOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<CustomPathOutline>
) : CropOutlineContainer<CustomPathOutline>

data class ImageMaskOutlineContainer(
    override var selectedIndex: Int = 0,
    override val outlines: List<ImageMaskOutline>
) : CropOutlineContainer<ImageMaskOutline>
