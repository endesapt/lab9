package com.example.weatherapplication.localization

import com.example.weatherapplication.currentLanguage
import com.example.weatherapplication.data.WeatherCondition

data class AppStrings(
    val appName: String,
    val searchHint: String,
    val addCity: String,
    val refresh: String,
    val loadingWeather: String,
    val enterCity: String,
    val cityNotFound: String,
    val cityAlreadyAdded: String,
    val cityAddedTemplate: String,
    val networkError: String,
    val cachedData: String,
    val staleCache: String,
    val temperature: String,
    val humidity: String,
    val windSpeed: String,
    val clear: String,
    val mainlyClear: String,
    val cloudy: String,
    val fog: String,
    val drizzle: String,
    val rain: String,
    val snow: String,
    val thunder: String
)

fun currentStrings(): AppStrings {
    val language = normalizeLanguage(currentLanguage())
    return STRINGS[language] ?: STRINGS.getValue("en")
}

fun AppStrings.cityAdded(city: String): String {
    return cityAddedTemplate.replace("{city}", city)
}

fun conditionLabel(condition: WeatherCondition, strings: AppStrings): String {
    return when (condition) {
        WeatherCondition.Clear -> strings.clear
        WeatherCondition.MainlyClear -> strings.mainlyClear
        WeatherCondition.Cloudy -> strings.cloudy
        WeatherCondition.Fog -> strings.fog
        WeatherCondition.Drizzle -> strings.drizzle
        WeatherCondition.Rain -> strings.rain
        WeatherCondition.Snow -> strings.snow
        WeatherCondition.Thunder -> strings.thunder
    }
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
        appName = "Weather Now",
        searchHint = "City name",
        addCity = "Add",
        refresh = "Refresh",
        loadingWeather = "Loading weather...",
        enterCity = "Enter a city name.",
        cityNotFound = "City not found.",
        cityAlreadyAdded = "City already added.",
        cityAddedTemplate = "{city} added.",
        networkError = "Network error",
        cachedData = "Cached data",
        staleCache = "Cached (stale)",
        temperature = "Temperature",
        humidity = "Humidity",
        windSpeed = "Wind",
        clear = "Clear",
        mainlyClear = "Mostly clear",
        cloudy = "Cloudy",
        fog = "Fog",
        drizzle = "Drizzle",
        rain = "Rain",
        snow = "Snow",
        thunder = "Thunder"
    ),
    "ru" to AppStrings(
        appName = "Погода сейчас",
        searchHint = "Название города",
        addCity = "Добавить",
        refresh = "Обновить",
        loadingWeather = "Загрузка погоды...",
        enterCity = "Введите название города.",
        cityNotFound = "Город не найден.",
        cityAlreadyAdded = "Город уже в списке.",
        cityAddedTemplate = "Город {city} добавлен.",
        networkError = "Ошибка сети",
        cachedData = "Кешированные данные",
        staleCache = "Кеш (устарел)",
        temperature = "Температура",
        humidity = "Влажность",
        windSpeed = "Ветер",
        clear = "Ясно",
        mainlyClear = "Малооблачно",
        cloudy = "Облачно",
        fog = "Туман",
        drizzle = "Морось",
        rain = "Дождь",
        snow = "Снег",
        thunder = "Гроза"
    ),
    "be" to AppStrings(
        appName = "Надвор'е зараз",
        searchHint = "Назва горада",
        addCity = "Дадаць",
        refresh = "Абнавіць",
        loadingWeather = "Загрузка надвор'я...",
        enterCity = "Увядзіце назву горада.",
        cityNotFound = "Горад не знойдзены.",
        cityAlreadyAdded = "Горад ужо ў спісе.",
        cityAddedTemplate = "Горад {city} дададзены.",
        networkError = "Памылка сеткі",
        cachedData = "Кешаваныя даныя",
        staleCache = "Кеш (састарэлы)",
        temperature = "Тэмпература",
        humidity = "Вільготнасць",
        windSpeed = "Вецер",
        clear = "Ясна",
        mainlyClear = "Малааблочна",
        cloudy = "Аблочна",
        fog = "Туман",
        drizzle = "Імжа",
        rain = "Дождж",
        snow = "Снег",
        thunder = "Навальніца"
    )
)
