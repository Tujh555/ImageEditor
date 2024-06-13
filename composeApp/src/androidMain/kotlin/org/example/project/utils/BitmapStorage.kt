package org.example.project.utils

import android.graphics.Bitmap
import androidx.collection.LruCache
import kotlin.math.roundToInt

internal class BitmapStorage {
    private val maxCacheSize: Int = Runtime.getRuntime().run {
        val maxMemoryKb = (maxMemory() / 1024).toInt()
        (maxMemoryKb * (3f / 4f)).roundToInt()
    }

    private val lruCache = object : LruCache<Int, Bitmap>(maxCacheSize) {
        override fun sizeOf(key: Int, value: Bitmap): Int =
            value.byteCount / 1024

        override fun entryRemoved(evicted: Boolean, key: Int, oldValue: Bitmap, newValue: Bitmap?) {
            if (evicted) {
                oldValue.recycle()
            }
        }
    }

    private var lastCachedImageNumber = INITIAL_CACHED_IMAGE_NUMBER
        @Synchronized set

    val cacheSize
        get() = lruCache.size()

    fun put(bitmap: Bitmap) {
        lastCachedImageNumber++
        lruCache.put(lastCachedImageNumber, bitmap)
    }

    fun pop(): Bitmap? {
        if (lastCachedImageNumber == INITIAL_CACHED_IMAGE_NUMBER) {
            return null
        }

        val lastBitmap = lruCache[lastCachedImageNumber]
        lruCache.remove(lastCachedImageNumber)
        lastCachedImageNumber--

        return lastBitmap
    }

    fun clear() {
        lruCache.evictAll()
        lastCachedImageNumber = INITIAL_CACHED_IMAGE_NUMBER
    }

    companion object {
        private const val INITIAL_CACHED_IMAGE_NUMBER = -1
    }
}