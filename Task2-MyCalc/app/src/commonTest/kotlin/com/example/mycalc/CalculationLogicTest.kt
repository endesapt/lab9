package com.example.mycalc

import com.example.mycalc.model.Compounding
import com.example.mycalc.model.InputParams
import com.example.mycalc.model.calculateGrowth
import kotlin.test.Test
import kotlin.test.assertTrue

class CalculationLogicTest {
    @Test
    fun finalAmountGrowsWithPositiveRate() {
        val params = InputParams(principal = 1000.0, annualRatePercent = 5.0, years = 3, compounding = Compounding.Yearly)
        val result = calculateGrowth(params)
        assertTrue(result.finalAmount > params.principal)
    }

    @Test
    fun zeroRateKeepsPrincipal() {
        val params = InputParams(principal = 1000.0, annualRatePercent = 0.0, years = 3, compounding = Compounding.Monthly)
        val result = calculateGrowth(params)
        assertTrue(kotlin.math.abs(result.finalAmount - params.principal) < 0.01)
    }

    @Test
    fun seriesHasMultiplePoints() {
        val params = InputParams(principal = 500.0, annualRatePercent = 4.0, years = 2, compounding = Compounding.Quarterly)
        val result = calculateGrowth(params)
        assertTrue(result.series.size >= 3)
    }
}
