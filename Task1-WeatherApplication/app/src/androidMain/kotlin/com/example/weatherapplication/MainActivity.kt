package com.example.weatherapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.weatherapplication.cache.createCacheStore
import com.example.weatherapplication.data.createHttpClient
import com.example.weatherapplication.ui.WeatherApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deps = AppDependencies(
            httpClient = createHttpClient(),
            cacheStore = createCacheStore(PlatformContext(this)),
            platform = PlatformKind.Android
        )

        setContent {
            WeatherApp(deps)
        }
    }
}
