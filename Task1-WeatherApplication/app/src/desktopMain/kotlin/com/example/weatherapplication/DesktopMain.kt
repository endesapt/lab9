package com.example.weatherapplication

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.weatherapplication.cache.createCacheStore
import com.example.weatherapplication.data.createHttpClient
import com.example.weatherapplication.localization.currentStrings
import com.example.weatherapplication.ui.WeatherApp

fun main() = application {
    val deps = AppDependencies(
        httpClient = createHttpClient(),
        cacheStore = createCacheStore(),
        platform = PlatformKind.Desktop
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = currentStrings().appName
    ) {
        WeatherApp(deps)
    }
}
