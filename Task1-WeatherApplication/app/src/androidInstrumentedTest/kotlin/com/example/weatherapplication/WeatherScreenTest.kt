package com.example.weatherapplication

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.weatherapplication.data.City
import com.example.weatherapplication.data.CityWeather
import com.example.weatherapplication.data.CurrentWeather
import com.example.weatherapplication.data.WeatherCondition
import com.example.weatherapplication.ui.WeatherScreen
import com.example.weatherapplication.ui.WeatherUiState
import java.util.Locale
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class WeatherScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        Locale.setDefault(Locale.ENGLISH)
    }

    @Test
    fun showsSearchAndButtons() {
        composeTestRule.setContent {
            WeatherScreen(
                state = sampleState(),
                platform = PlatformKind.Android,
                onSearchQueryChange = {},
                onAddCity = {},
                onRefresh = {},
                onSelectCity = {}
            )
        }

        composeTestRule.onNodeWithTag("search_input").assertExists()
        composeTestRule.onNodeWithTag("add_button").assertExists()
        composeTestRule.onNodeWithTag("refresh_button").assertExists()
    }

    @Test
    fun showsCityCardWithWeather() {
        composeTestRule.setContent {
            WeatherScreen(
                state = sampleState(),
                platform = PlatformKind.Android,
                onSearchQueryChange = {},
                onAddCity = {},
                onRefresh = {},
                onSelectCity = {}
            )
        }

        composeTestRule.onNodeWithTag("city_card_Minsk").assertExists()
        composeTestRule.onNodeWithText("Temperature: 21 C").assertExists()
    }

    @Test
    fun showsCachedLabel() {
        composeTestRule.setContent {
            WeatherScreen(
                state = sampleState(isFromCache = true),
                platform = PlatformKind.Android,
                onSearchQueryChange = {},
                onAddCity = {},
                onRefresh = {},
                onSelectCity = {}
            )
        }

        composeTestRule.onNodeWithText("Cached data").assertExists()
    }

    private fun sampleState(isFromCache: Boolean = false): WeatherUiState {
        val city = City(name = "Minsk", country = "Belarus", latitude = 53.9, longitude = 27.56)
        val weather = CurrentWeather(
            temperatureC = 21,
            humidityPercent = 40,
            windSpeedKmh = 5,
            condition = WeatherCondition.Clear
        )
        val entry = CityWeather(city = city, weather = weather, isFromCache = isFromCache)
        return WeatherUiState(cities = listOf(entry))
    }
}
