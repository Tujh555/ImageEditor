package implementation.data.mapper

import comexampleprojectshareddatabase.ImageEntity
import implementation.domain.models.Image

internal fun ImageEntity.toDomain() = Image(
    id = id,
    name = name,
    saveDate = save_date,
    path = path,
)

internal fun Image.toDb() = ImageEntity(
    id = id,
    name = name,
    save_date = saveDate,
    path = path
)