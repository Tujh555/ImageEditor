package implementation.data.entity.adapter

import app.cash.sqldelight.ColumnAdapter
import comexampleprojectshareddatabase.ImageEntity
import kotlinx.datetime.Instant

internal val imageEntityAdapter = ImageEntity.Adapter(
    save_dateAdapter = InstantColumnAdapter()
)

private class InstantColumnAdapter : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long): Instant = Instant.fromEpochSeconds(databaseValue)

    override fun encode(value: Instant): Long = value.epochSeconds
}