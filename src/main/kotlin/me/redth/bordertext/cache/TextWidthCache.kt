package me.redth.bordertext.cache

object TextWidthCache {
    private val CACHE = HashMap<String, Cache>()

    fun getCachedWidth(text: String): Int? {
        val cache = CACHE[text]
        return cache?.getWidth()
    }

    fun putCache(text: String, width: Int) {
        CACHE[text] = Cache(width)
    }

    fun clearAll() {
        CACHE.clear()
    }

    fun cleanUp() {
        CACHE.entries.removeIf { (_, value): Map.Entry<String, Cache> -> !value.usedThisFrame }
    }

    class Cache(private val width: Int) {
        var usedThisFrame = false

        fun getWidth(): Int {
            usedThisFrame = true
            return width
        }
    }
}
