package com.example.weatherapplication

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.example.weatherapplication.cache.createCacheStore
import com.example.weatherapplication.data.createHttpClient
import com.example.weatherapplication.ui.WeatherApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val deps = AppDependencies(
        httpClient = createHttpClient(),
        cacheStore = createCacheStore(),
        platform = PlatformKind.Web
    )

    CanvasBasedWindow("Weather") {
        WeatherApp(deps)
    }
}
