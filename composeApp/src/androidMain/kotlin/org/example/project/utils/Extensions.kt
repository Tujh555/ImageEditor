package org.example.project.utils

import android.content.Context
import android.os.Build
import android.text.format.Formatter
import kotlin.math.pow

private const val KB = 1024.0
private const val QUAD_POW = 2
private const val CUBIC_POW = 3
private const val THOUSAND = 1000.0

/**
 * Starting from android Oreo file size count kb as 1000
 */
fun Context.toHumanReadableFileSize(size: Long): String {
    val fileSize = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val kb = KB
        val mb = kb.pow(QUAD_POW)
        val gb = kb.pow(CUBIC_POW)
        when {
            size < kb -> size
            size >= kb && size < mb -> size / kb * THOUSAND
            size >= mb && size < gb -> size / mb * THOUSAND.pow(QUAD_POW)
            else -> size
        }.toLong()
    } else {
        size
    }
    return Formatter.formatFileSize(
        this,
        fileSize
    )
}