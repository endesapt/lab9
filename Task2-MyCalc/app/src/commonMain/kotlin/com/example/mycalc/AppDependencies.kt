package com.example.mycalc

import com.example.mycalc.cache.CacheStore

data class AppDependencies(
    val cacheStore: CacheStore,
    val platform: PlatformKind
)
