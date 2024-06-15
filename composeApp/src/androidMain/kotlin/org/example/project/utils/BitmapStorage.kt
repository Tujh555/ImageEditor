package org.example.project.utils

import android.graphics.Bitmap
import androidx.collection.LruCache
import java.util.concurrent.atomic.AtomicInteger
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

    private val lastCachedImageNumber = AtomicInteger(INITIAL_CACHED_IMAGE_NUMBER)

    val cacheSize
        get() = lruCache.size()

    fun put(bitmap: Bitmap) {
        lruCache.put(lastCachedImageNumber.incrementAndGet(), bitmap)
    }

    fun pop(): Bitmap? {
        if (lastCachedImageNumber.get() == INITIAL_CACHED_IMAGE_NUMBER) {
            return null
        }

        val lastBitmap = lruCache[lastCachedImageNumber.get()]
        lruCache.remove(lastCachedImageNumber.get())
        lastCachedImageNumber.decrementAndGet()

        return lastBitmap
    }

    fun clear() {
        lruCache.evictAll()
        lastCachedImageNumber.set(INITIAL_CACHED_IMAGE_NUMBER)
    }

    companion object {
        private const val INITIAL_CACHED_IMAGE_NUMBER = -1
    }
}