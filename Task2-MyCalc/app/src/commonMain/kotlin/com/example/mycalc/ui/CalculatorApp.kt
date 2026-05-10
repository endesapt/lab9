package com.example.mycalc.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.mycalc.AppDependencies
import com.example.mycalc.cache.HistoryRepository
import com.example.mycalc.localization.currentStrings

@Composable
fun CalculatorApp(deps: AppDependencies) {
    val repository = remember(deps.cacheStore) { HistoryRepository(deps.cacheStore) }
    val presenter = remember(repository) { CalculatorPresenter(repository, ::currentStrings) }
    val state by presenter.state.collectAsState()

    LaunchedEffect(Unit) {
        presenter.start()
    }

    CalculatorTheme {
        CalculatorScreen(
            state = state,
            platform = deps.platform,
            onPrincipalChange = presenter::onPrincipalChange,
            onRateChange = presenter::onRateChange,
            onYearsChange = presenter::onYearsChange,
            onCompoundingChange = presenter::onCompoundingChange,
            onCalculate = presenter::calculateAndSave,
            onClearHistory = presenter::clearHistory,
            onChartScaleChange = presenter::onChartScaleChange
        )
    }
}

@Composable
private fun CalculatorTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = colors, content = content)
}
