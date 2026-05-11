package com.example.mycalc.ui

import com.example.mycalc.cache.HistoryRepository
import com.example.mycalc.localization.AppStrings
import com.example.mycalc.model.CalculationResult
import com.example.mycalc.model.Compounding
import com.example.mycalc.model.HistoryEntry
import com.example.mycalc.model.InputParams
import com.example.mycalc.model.ValidationResult
import com.example.mycalc.model.calculateGrowth
import com.example.mycalc.currentTimeMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val MAX_YEARS = 50

data class CalculatorUiState(
    val principal: String = "10000",
    val annualRate: String = "8",
    val years: Int = 5,
    val compounding: Compounding = Compounding.Monthly,
    val result: CalculationResult? = null,
    val history: List<HistoryEntry> = emptyList(),
    val validation: ValidationResult = ValidationResult(),
    val chartScale: Float = 1f
)

class CalculatorPresenter(
    private val repository: HistoryRepository,
    private val stringsProvider: () -> AppStrings
) {
    private val _state = MutableStateFlow(CalculatorUiState())
    val state: StateFlow<CalculatorUiState> = _state.asStateFlow()

    fun start() {
        val history = repository.loadHistory()
        _state.update { it.copy(history = history) }
    }

    fun onPrincipalChange(value: String) {
        _state.update { it.copy(principal = value, validation = ValidationResult()) }
    }

    fun onRateChange(value: String) {
        _state.update { it.copy(annualRate = value, validation = ValidationResult()) }
    }

    fun onYearsChange(value: Int) {
        val clamped = value.coerceIn(1, MAX_YEARS)
        _state.update { it.copy(years = clamped, validation = ValidationResult()) }
    }

    fun onCompoundingChange(value: Compounding) {
        _state.update { it.copy(compounding = value) }
    }

    fun onChartScaleChange(value: Float) {
        val clamped = value.coerceIn(0.6f, 2.4f)
        _state.update { it.copy(chartScale = clamped) }
    }

    fun calculateAndSave() {
        val validation = validate()
        if (!validation.isValid) {
            _state.update { it.copy(validation = validation) }
            return
        }

        val params = InputParams(
            principal = _state.value.principal.toDouble(),
            annualRatePercent = _state.value.annualRate.toDouble(),
            years = _state.value.years,
            compounding = _state.value.compounding
        )

        try {
            val result = calculateGrowth(params)
            val entry = HistoryEntry(
                timestampMillis = currentTimeMillis(),
                params = params,
                result = result
            )
            val updatedHistory = (_state.value.history + entry).takeLast(20)
            repository.saveHistory(updatedHistory)
            _state.update { it.copy(result = result, history = updatedHistory, validation = ValidationResult()) }
        } catch (exception: Exception) {
            println("Calculation error: ${exception.message}")
        }
    }

    fun clearHistory() {
        repository.clearHistory()
        _state.update { it.copy(history = emptyList()) }
    }

    private fun validate(): ValidationResult {
        val strings = stringsProvider()
        val principal = _state.value.principal.toDoubleOrNull()
        val rate = _state.value.annualRate.toDoubleOrNull()
        val years = _state.value.years

        return ValidationResult(
            principalError = if (principal == null || principal <= 0.0) strings.invalidPrincipal else null,
            rateError = if (rate == null || rate < 0.0) strings.invalidRate else null,
            yearsError = if (years < 1) strings.invalidYears else null
        )
    }
}
