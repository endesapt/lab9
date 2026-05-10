package com.example.weatherapplication

import java.io.Serializable

data class CityWeatherItem(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    var currentTemperature: String = "--",
    var currentSummary: String = "Loading..."
) : Serializable
