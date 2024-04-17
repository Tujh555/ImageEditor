package implementation.time

import api.time.DateFormatter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

internal class DateFormatterImpl : DateFormatter {
    override fun format(time: Instant): String {
        val localDate = time.toLocalDateTime(TimeZone.currentSystemDefault())

        return formatter.format(localDate)
    }

    companion object {
        private val formatter = LocalDateTime.Format {
            monthName(MonthNames.ENGLISH_ABBREVIATED)
            char(' ')
            dayOfMonth()
            chars(", ")
            year()
        }
    }
}