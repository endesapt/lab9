package com.example.weatherapplication

import androidx.compose.ui.window.ComposeUIViewController
import com.example.weatherapplication.cache.createCacheStore
import com.example.weatherapplication.data.createHttpClient
import com.example.weatherapplication.ui.WeatherApp

fun MainViewController() = ComposeUIViewController {
    val deps = AppDependencies(
        httpClient = createHttpClient(),
        cacheStore = createCacheStore(),
        platform = PlatformKind.IOS
    )
    WeatherApp(deps)
}
