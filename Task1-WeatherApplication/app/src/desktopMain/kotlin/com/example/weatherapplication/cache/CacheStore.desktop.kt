package com.example.weatherapplication.cache

import com.example.weatherapplication.PlatformContext
import java.util.prefs.Preferences

actual fun createCacheStore(context: PlatformContext?): CacheStore {
    return DesktopCacheStore()
}

private class DesktopCacheStore : CacheStore {
    private val prefs = Preferences.userRoot().node("weather_cache")

    override fun read(key: String): String? = prefs.get(key, null)

    override fun write(key: String, value: String) {
        prefs.put(key, value)
    }

    override fun remove(key: String) {
        prefs.remove(key)
    }
}
