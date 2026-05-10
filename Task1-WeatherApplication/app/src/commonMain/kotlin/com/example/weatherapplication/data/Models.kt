package com.example.weatherapplication.data

data class City(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)

data class CityWeather(
    val city: City,
    val weather: CurrentWeather? = null,
    val isFromCache: Boolean = false,
    val isStale: Boolean = false
)

data class CurrentWeather(
    val temperatureC: Int,
    val humidityPercent: Int,
    val windSpeedKmh: Int,
    val condition: WeatherCondition
)

enum class WeatherCondition {
    Clear,
    MainlyClear,
    Cloudy,
    Fog,
    Drizzle,
    Rain,
    Snow,
    Thunder
}

fun conditionFromCode(code: Int): WeatherCondition {
    return when (code) {
        0 -> WeatherCondition.Clear
        1, 2 -> WeatherCondition.MainlyClear
        3 -> WeatherCondition.Cloudy
        45, 48 -> WeatherCondition.Fog
        51, 53, 55, 56, 57 -> WeatherCondition.Drizzle
        61, 63, 65, 66, 67, 80, 81, 82 -> WeatherCondition.Rain
        71, 73, 75, 77, 85, 86 -> WeatherCondition.Snow
        95, 96, 99 -> WeatherCondition.Thunder
        else -> WeatherCondition.Cloudy
    }
}
