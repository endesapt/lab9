package com.example.weatherapplication

import com.example.weatherapplication.cache.CacheStore
import io.ktor.client.HttpClient

data class AppDependencies(
    val httpClient: HttpClient,
    val cacheStore: CacheStore,
    val platform: PlatformKind
)
