package com.example.mycalc.model

import kotlinx.serialization.Serializable

@Serializable
data class InputParams(
    val principal: Double,
    val annualRatePercent: Double,
    val years: Int,
    val compounding: Compounding
)

@Serializable
data class CalculationResult(
    val finalAmount: Double,
    val profit: Double,
    val series: List<GraphPoint>
)

@Serializable
data class GraphPoint(
    val index: Int,
    val amount: Double
)

@Serializable
data class HistoryEntry(
    val timestampMillis: Long,
    val params: InputParams,
    val result: CalculationResult
)

@Serializable
enum class Compounding(val periodsPerYear: Int) {
    Monthly(12),
    Quarterly(4),
    Yearly(1)
}
