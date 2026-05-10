package com.example.mycalc.cache

import android.content.Context
import com.example.mycalc.PlatformContext

actual fun createCacheStore(context: PlatformContext?): CacheStore {
    val safeContext = context ?: error("Android context is required")
    return AndroidCacheStore(safeContext)
}

private class AndroidCacheStore(context: Context) : CacheStore {
    private val prefs = context.getSharedPreferences("calc_cache", Context.MODE_PRIVATE)

    override fun read(key: String): String? = prefs.getString(key, null)

    override fun write(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }
}
