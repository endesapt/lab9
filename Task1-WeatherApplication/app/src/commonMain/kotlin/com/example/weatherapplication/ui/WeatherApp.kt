package com.example.weatherapplication.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.weatherapplication.AppDependencies
import com.example.weatherapplication.cache.WeatherCache
import com.example.weatherapplication.data.OpenMeteoApi
import com.example.weatherapplication.data.WeatherRepository
import com.example.weatherapplication.localization.currentStrings

@Composable
fun WeatherApp(deps: AppDependencies) {
    val repository = remember(deps) {
        WeatherRepository(OpenMeteoApi(deps.httpClient), WeatherCache(deps.cacheStore))
    }
    val presenter = remember(repository) {
        WeatherPresenter(repository, ::currentStrings)
    }
    val state by presenter.state.collectAsState()

    LaunchedEffect(Unit) {
        presenter.start()
    }

    DisposableEffect(Unit) {
        onDispose { presenter.clear() }
    }

    WeatherTheme {
        WeatherScreen(
            state = state,
            platform = deps.platform,
            onSearchQueryChange = presenter::onSearchQueryChanged,
            onAddCity = presenter::addCity,
            onRefresh = presenter::refreshAll,
            onSelectCity = presenter::selectCity
        )
    }
}

@Composable
private fun WeatherTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = colors, content = content)
}
