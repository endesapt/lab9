package com.example.weatherapplication.cache

import com.example.weatherapplication.data.City
import com.example.weatherapplication.data.CurrentWeather
import com.example.weatherapplication.data.WeatherCondition
import com.example.weatherapplication.currentTimeMillis
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val CITIES_KEY = "cities"
private const val WEATHER_KEY_PREFIX = "weather_"
private const val CACHE_TTL_MILLIS = 30 * 60 * 1000L

data class CacheResult(
    val weather: CurrentWeather,
    val isStale: Boolean
)

class WeatherCache(private val store: CacheStore) {
    private val json = Json { ignoreUnknownKeys = true }

    fun loadCities(): List<City> {
        val raw = store.read(CITIES_KEY) ?: return emptyList()
        return runCatching {
            json.decodeFromString<List<CachedCity>>(raw).map { it.toCity() }
        }.getOrElse { emptyList() }
    }

    fun saveCities(cities: List<City>) {
        val raw = json.encodeToString(cities.map { CachedCity.fromCity(it) })
        store.write(CITIES_KEY, raw)
    }

    fun saveCurrent(city: City, weather: CurrentWeather) {
        val cached = CachedCurrentWeather.fromWeather(weather, currentTimeMillis())
        store.write(weatherKey(city), json.encodeToString(cached))
    }

    fun loadCurrent(city: City): CacheResult? {
        val raw = store.read(weatherKey(city)) ?: return null
        val cached = runCatching {
            json.decodeFromString<CachedCurrentWeather>(raw)
        }.getOrNull() ?: return null

        val weather = cached.toWeather()
        val stale = currentTimeMillis() - cached.updatedAtMillis > CACHE_TTL_MILLIS
        return CacheResult(weather, stale)
    }

    private fun weatherKey(city: City): String {
        return WEATHER_KEY_PREFIX + city.latitude + "_" + city.longitude
    }
}

@Serializable
data class CachedCity(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
) {
    fun toCity(): City {
        return City(name = name, country = country, latitude = latitude, longitude = longitude)
    }

    companion object {
        fun fromCity(city: City): CachedCity {
            return CachedCity(
                name = city.name,
                country = city.country,
                latitude = city.latitude,
                longitude = city.longitude
            )
        }
    }
}

@Serializable
data class CachedCurrentWeather(
    val temperatureC: Int,
    val humidityPercent: Int,
    val windSpeedKmh: Int,
    val condition: WeatherCondition,
    val updatedAtMillis: Long
) {
    fun toWeather(): CurrentWeather {
        return CurrentWeather(
            temperatureC = temperatureC,
            humidityPercent = humidityPercent,
            windSpeedKmh = windSpeedKmh,
            condition = condition
        )
    }

    companion object {
        fun fromWeather(weather: CurrentWeather, updatedAtMillis: Long): CachedCurrentWeather {
            return CachedCurrentWeather(
                temperatureC = weather.temperatureC,
                humidityPercent = weather.humidityPercent,
                windSpeedKmh = weather.windSpeedKmh,
                condition = weather.condition,
                updatedAtMillis = updatedAtMillis
            )
        }
    }
}
