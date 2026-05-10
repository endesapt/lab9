package com.example.mycalc.localization

import com.example.mycalc.currentLanguage

data class AppStrings(
    val appName: String,
    val title: String,
    val principal: String,
    val annualRate: String,
    val years: String,
    val compounding: String,
    val monthly: String,
    val quarterly: String,
    val yearly: String,
    val calculate: String,
    val saveResult: String,
    val history: String,
    val clearHistory: String,
    val finalAmount: String,
    val profit: String,
    val invalidPrincipal: String,
    val invalidRate: String,
    val invalidYears: String,
    val chartTitle: String,
    val zoomHint: String
)

fun currentStrings(): AppStrings {
    val language = normalizeLanguage(currentLanguage())
    return STRINGS[language] ?: STRINGS.getValue("en")
}

private fun normalizeLanguage(raw: String): String {
    val language = raw.lowercase()
    return when {
        language.startsWith("ru") -> "ru"
        language.startsWith("be") -> "be"
        language.startsWith("en") -> "en"
        else -> "en"
    }
}

private val STRINGS = mapOf(
    "en" to AppStrings(
        appName = "Finance Calculator",
        title = "Capital growth",
        principal = "Initial amount",
        annualRate = "Annual rate (%)",
        years = "Years",
        compounding = "Compounding",
        monthly = "Monthly",
        quarterly = "Quarterly",
        yearly = "Yearly",
        calculate = "Calculate",
        saveResult = "Save",
        history = "History",
        clearHistory = "Clear history",
        finalAmount = "Final amount",
        profit = "Profit",
        invalidPrincipal = "Enter a positive amount.",
        invalidRate = "Enter a valid rate.",
        invalidYears = "Years must be at least 1.",
        chartTitle = "Growth chart",
        zoomHint = "Pinch or scroll to zoom"
    ),
    "ru" to AppStrings(
        appName = "Финансовый калькулятор",
        title = "Рост капитала",
        principal = "Начальная сумма",
        annualRate = "Годовая ставка (%)",
        years = "Срок (лет)",
        compounding = "Капитализация",
        monthly = "Ежемесячно",
        quarterly = "Ежеквартально",
        yearly = "Ежегодно",
        calculate = "Рассчитать",
        saveResult = "Сохранить",
        history = "История",
        clearHistory = "Очистить",
        finalAmount = "Итоговая сумма",
        profit = "Прибыль",
        invalidPrincipal = "Введите сумму больше нуля.",
        invalidRate = "Введите корректную ставку.",
        invalidYears = "Срок должен быть не меньше 1.",
        chartTitle = "График роста",
        zoomHint = "Масштабируйте жестом"
    ),
    "be" to AppStrings(
        appName = "Фінансавы калькулятар",
        title = "Рост капіталу",
        principal = "Пачатковая сума",
        annualRate = "Гадавая стаўка (%)",
        years = "Тэрмін (гадоў)",
        compounding = "Капіталізацыя",
        monthly = "Штомесяц",
        quarterly = "Штоквартал",
        yearly = "Штогод",
        calculate = "Разлічыць",
        saveResult = "Захаваць",
        history = "Гісторыя",
        clearHistory = "Ачысціць",
        finalAmount = "Канчатковая сума",
        profit = "Прыбытак",
        invalidPrincipal = "Увядзіце суму больш за нуль.",
        invalidRate = "Увядзіце карэктную стаўку.",
        invalidYears = "Тэрмін павінен быць не менш за 1.",
        chartTitle = "Графік росту",
        zoomHint = "Маштабіруйце жэстам"
    )
)
