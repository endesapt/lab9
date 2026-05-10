package com.example.mycalc.model

import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt

fun calculateGrowth(params: InputParams): CalculationResult {
    val rate = params.annualRatePercent / 100.0
    val periods = params.years * params.compounding.periodsPerYear
    var amount = params.principal
    val points = mutableListOf(GraphPoint(0, amount))

    for (index in 1..periods) {
        amount *= (1.0 + rate / params.compounding.periodsPerYear)
        if (periods <= 24 || index % params.compounding.periodsPerYear == 0) {
            points.add(GraphPoint(index, amount))
        }
    }

    return CalculationResult(
        finalAmount = amount,
        profit = amount - params.principal,
        series = points
    )
}

fun formatMoney(value: Double): String {
    val cents = (value * 100.0).roundToInt()
    val whole = cents / 100
    val fraction = (cents % 100).absoluteValue
    return "$whole.${fraction.toString().padStart(2, '0')}"
}
