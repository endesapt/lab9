package com.example.weatherapplication.ui

import com.example.weatherapplication.data.City
import com.example.weatherapplication.data.CityWeather
import com.example.weatherapplication.data.WeatherRepository
import com.example.weatherapplication.localization.AppStrings
import com.example.weatherapplication.localization.cityAdded
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val DEFAULT_CITIES = listOf(
    City(name = "Minsk", country = "Belarus", latitude = 53.9006, longitude = 27.5590),
    City(name = "Vitebsk", country = "Belarus", latitude = 55.1904, longitude = 30.2049)
)

data class WeatherUiState(
    val cities: List<CityWeather> = emptyList(),
    val selectedCityIndex: Int = 0,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val statusMessage: String? = null
)

class WeatherPresenter(
    private val repository: WeatherRepository,
    private val stringsProvider: () -> AppStrings
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _state = MutableStateFlow(WeatherUiState())
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    fun start() {
        scope.launch {
            val initial = repository.loadCities(DEFAULT_CITIES)
            _state.update { it.copy(cities = initial.map { city -> CityWeather(city) }) }
            refreshAll()
        }
    }

    fun clear() {
        scope.cancel()
    }

    fun onSearchQueryChanged(value: String) {
        _state.update { it.copy(searchQuery = value, statusMessage = null) }
    }

    fun selectCity(index: Int) {
        _state.update {
            val maxIndex = (it.cities.size - 1).coerceAtLeast(0)
            it.copy(selectedCityIndex = index.coerceIn(0, maxIndex))
        }
    }

    fun addCity() {
        val query = _state.value.searchQuery.trim()
        if (query.isEmpty()) {
            _state.update { it.copy(statusMessage = strings().enterCity) }
            return
        }

        scope.launch {
            _state.update { it.copy(isLoading = true, statusMessage = strings().loadingWeather) }
            val found = runCatching { repository.searchCity(query) }.getOrNull()
            if (found == null) {
                _state.update {
                    it.copy(isLoading = false, statusMessage = strings().cityNotFound)
                }
                return@launch
            }

            val exists = _state.value.cities.any { it.city.name.equals(found.name, ignoreCase = true) }
            if (exists) {
                _state.update {
                    it.copy(isLoading = false, statusMessage = strings().cityAlreadyAdded)
                }
                return@launch
            }

            val updated = _state.value.cities + CityWeather(found)
            repository.saveCities(updated.map { it.city })
            _state.update {
                it.copy(
                    cities = updated,
                    searchQuery = "",
                    statusMessage = strings().cityAdded(found.name)
                )
            }

            refreshCity(found.name)
        }
    }

    fun refreshAll() {
        scope.launch {
            val current = _state.value.cities
            if (current.isEmpty()) return@launch

            _state.update { it.copy(isLoading = true, statusMessage = strings().loadingWeather) }

            val refreshed = coroutineScope {
                current.map { entry ->
                    async { refreshEntry(entry) }
                }.awaitAll()
            }

            val usedCache = refreshed.any { it.isFromCache }
            val usedStale = refreshed.any { it.isStale }
            val hadMissing = refreshed.any { it.weather == null }

            val message = when {
                hadMissing -> strings().networkError
                usedStale -> strings().staleCache
                usedCache -> strings().cachedData
                else -> null
            }

            _state.update { it.copy(cities = refreshed, isLoading = false, statusMessage = message) }
        }
    }

    private suspend fun refreshEntry(entry: CityWeather): CityWeather {
        return try {
            val result = repository.fetchCurrent(entry.city)
            entry.copy(
                weather = result.weather,
                isFromCache = result.fromCache,
                isStale = result.isStale
            )
        } catch (exception: Exception) {
            println("Weather error: ${exception.message}")
            entry
        }
    }

    private fun refreshCity(cityName: String) {
        scope.launch {
            val current = _state.value.cities
            val refreshed = coroutineScope {
                current.map { entry ->
                    async {
                        if (entry.city.name == cityName) refreshEntry(entry) else entry
                    }
                }.awaitAll()
            }
            _state.update { it.copy(cities = refreshed, isLoading = false) }
        }
    }

    private fun strings(): AppStrings = stringsProvider()
}
