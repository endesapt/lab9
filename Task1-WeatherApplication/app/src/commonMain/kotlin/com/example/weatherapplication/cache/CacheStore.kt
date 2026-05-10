package com.example.weatherapplication.cache

import com.example.weatherapplication.PlatformContext

interface CacheStore {
    fun read(key: String): String?
    fun write(key: String, value: String)
    fun remove(key: String)
}

expect fun createCacheStore(context: PlatformContext? = null): CacheStore
