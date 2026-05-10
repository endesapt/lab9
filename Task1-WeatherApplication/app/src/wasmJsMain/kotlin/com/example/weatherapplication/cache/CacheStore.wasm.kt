package com.example.weatherapplication.cache

import com.example.weatherapplication.PlatformContext
import kotlinx.browser.localStorage

actual fun createCacheStore(context: PlatformContext?): CacheStore {
    return WebCacheStore()
}

private class WebCacheStore : CacheStore {
    override fun read(key: String): String? = localStorage.getItem(key)

    override fun write(key: String, value: String) {
        localStorage.setItem(key, value)
    }

    override fun remove(key: String) {
        localStorage.removeItem(key)
    }
}
