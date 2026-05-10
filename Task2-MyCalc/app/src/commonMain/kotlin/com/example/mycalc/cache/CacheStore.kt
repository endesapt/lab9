package com.example.mycalc.cache

import com.example.mycalc.PlatformContext

interface CacheStore {
    fun read(key: String): String?
    fun write(key: String, value: String)
    fun remove(key: String)
}

expect fun createCacheStore(context: PlatformContext? = null): CacheStore
