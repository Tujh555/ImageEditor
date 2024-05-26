package api.time

import kotlinx.datetime.Instant

interface DateFormatter {
    fun format(time: Instant): String
}