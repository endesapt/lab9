package com.example.weatherapplication.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class OpenMeteoApi(private val client: HttpClient) {
    suspend fun searchCity(query: String): City? {
        val response: GeoResponse = client.get(GEO_URL) {
            parameter("name", query)
            parameter("count", 1)
            parameter("language", "en")
            parameter("format", "json")
        }.body()

        val result = response.results?.firstOrNull() ?: return null
        return City(
            name = result.name,
            country = result.country ?: "Unknown",
            latitude = result.latitude,
            longitude = result.longitude
        )
    }

    suspend fun fetchCurrent(city: City): CurrentWeather {
        val response: ForecastResponse = client.get(FORECAST_URL) {
            parameter("latitude", city.latitude)
            parameter("longitude", city.longitude)
            parameter("current", "temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m")
            parameter("timezone", "auto")
        }.body()

        val current = response.current
        return CurrentWeather(
            temperatureC = current.temperature2m.toInt(),
            humidityPercent = current.relativeHumidity2m.toInt(),
            windSpeedKmh = current.windSpeed10m.toInt(),
            condition = conditionFromCode(current.weatherCode)
        )
    }

    private companion object {
        const val GEO_URL = "https://geocoding-api.open-meteo.com/v1/search"
        const val FORECAST_URL = "https://api.open-meteo.com/v1/forecast"
    }
}

@Serializable
data class GeoResponse(
    val results: List<GeoResult>? = null
)

@Serializable
data class GeoResult(
    val name: String,
    val country: String? = null,
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class ForecastResponse(
    val current: CurrentDto
)

@Serializable
data class CurrentDto(
    @SerialName("temperature_2m")
    val temperature2m: Double,
    @SerialName("relative_humidity_2m")
    val relativeHumidity2m: Double,
    @SerialName("weather_code")
    val weatherCode: Int,
    @SerialName("wind_speed_10m")
    val windSpeed10m: Double
)
