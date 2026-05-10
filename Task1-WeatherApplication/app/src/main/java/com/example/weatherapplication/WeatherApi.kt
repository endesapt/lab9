package com.example.weatherapplication

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object WeatherApi {

    fun findCity(query: String): CityWeatherItem? {
        val encodedQuery = URLEncoder.encode(query, Charsets.UTF_8.name())
        val url = "https://geocoding-api.open-meteo.com/v1/search?name=$encodedQuery&count=1&language=ru&format=json"
        val json = readJson(url)
        val results = json.optJSONArray("results") ?: return null
        if (results.length() == 0) return null

        val result = results.getJSONObject(0)
        return CityWeatherItem(
            name = result.getString("name"),
            country = result.optString("country", "Unknown"),
            latitude = result.getDouble("latitude"),
            longitude = result.getDouble("longitude")
        )
    }

    fun fetchCurrent(latitude: Double, longitude: Double): CurrentWeather {
        val url = buildString {
            append("https://api.open-meteo.com/v1/forecast?")
            append("latitude=$latitude&longitude=$longitude")
            append("&current=temperature_2m,weather_code,wind_speed_10m")
            append("&timezone=auto")
        }
        val json = readJson(url)
        val current = json.getJSONObject("current")
        return CurrentWeather(
            temperature = current.getDouble("temperature_2m").toInt(),
            description = WeatherDescriptions.describe(current.getInt("weather_code")),
            windSpeed = current.getDouble("wind_speed_10m").toInt()
        )
    }

    fun fetchDetails(latitude: Double, longitude: Double): WeatherDetails {
        val url = buildString {
            append("https://api.open-meteo.com/v1/forecast?")
            append("latitude=$latitude&longitude=$longitude")
            append("&current=temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m,weather_code")
            append("&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum")
            append("&forecast_days=7&timezone=auto")
        }
        val json = readJson(url)
        val current = json.getJSONObject("current")
        val daily = json.getJSONObject("daily")
        val time = daily.getJSONArray("time")
        val maxTemps = daily.getJSONArray("temperature_2m_max")
        val minTemps = daily.getJSONArray("temperature_2m_min")
        val codes = daily.getJSONArray("weather_code")
        val precipitation = daily.getJSONArray("precipitation_sum")

        val forecast = mutableListOf<String>()
        for (index in 0 until time.length()) {
            forecast += "${time.getString(index)}: ${minTemps.getDouble(index).toInt()} C .. " +
                "${maxTemps.getDouble(index).toInt()} C, " +
                "${WeatherDescriptions.describe(codes.getInt(index))}, " +
                "precipitation ${precipitation.getDouble(index)} mm"
        }

        return WeatherDetails(
            currentTemperature = current.getDouble("temperature_2m"),
            apparentTemperature = current.getDouble("apparent_temperature"),
            humidity = current.getInt("relative_humidity_2m"),
            windSpeed = current.getDouble("wind_speed_10m"),
            description = WeatherDescriptions.describe(current.getInt("weather_code")),
            forecast = forecast
        )
    }

    private fun readJson(urlString: String): JSONObject {
        val connection = URL(urlString).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.inputStream.bufferedReader().use { reader ->
            return JSONObject(reader.readText())
        }
    }
}

data class CurrentWeather(
    val temperature: Int,
    val description: String,
    val windSpeed: Int
)

data class WeatherDetails(
    val currentTemperature: Double,
    val apparentTemperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val description: String,
    val forecast: List<String>
)
