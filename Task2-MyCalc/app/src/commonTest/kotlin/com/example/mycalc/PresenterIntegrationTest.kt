package com.example.mycalc

import com.example.mycalc.cache.HistoryRepository
import com.example.mycalc.localization.AppStrings
import com.example.mycalc.model.Compounding
import com.example.mycalc.ui.CalculatorPresenter
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PresenterIntegrationTest {
    @Test
    fun calculateAddsHistory() {
        val presenter = CalculatorPresenter(HistoryRepository(PresenterTestCacheStore()), ::fakeStrings)
        presenter.onPrincipalChange("1000")
        presenter.onRateChange("5")
        presenter.onYearsChange(2)
        presenter.onCompoundingChange(Compounding.Yearly)
        presenter.calculateAndSave()
        val state = presenter.state.value
        assertNotNull(state.result)
        assertTrue(state.history.isNotEmpty())
    }
}

private fun fakeStrings(): AppStrings = AppStrings(
    appName = "",
    title = "",
    principal = "",
    annualRate = "",
    years = "",
    compounding = "",
    monthly = "",
    quarterly = "",
    yearly = "",
    calculate = "",
    saveResult = "",
    history = "",
    clearHistory = "",
    finalAmount = "",
    profit = "",
    invalidPrincipal = "bad",
    invalidRate = "bad",
    invalidYears = "bad",
    chartTitle = "",
    zoomHint = ""
)

private class PresenterTestCacheStore : com.example.mycalc.cache.CacheStore {
    private val data = mutableMapOf<String, String>()

    override fun read(key: String): String? = data[key]

    override fun write(key: String, value: String) {
        data[key] = value
    }

    override fun remove(key: String) {
        data.remove(key)
    }
}
