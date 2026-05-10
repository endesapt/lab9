package com.example.weatherapplication

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread

class WeatherDetailsActivity : AppCompatActivity() {

    private lateinit var headerText: TextView
    private lateinit var currentText: TextView
    private lateinit var forecastText: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        headerText = findViewById(R.id.headerText)
        currentText = findViewById(R.id.currentText)
        forecastText = findViewById(R.id.forecastText)
        progressBar = findViewById(R.id.detailsProgressBar)

        val city = intent.getSerializableExtra(EXTRA_CITY) as? CityWeatherItem ?: run {
            finish()
            return
        }

        headerText.text = "${city.name}, ${city.country}"
        currentText.text = getString(R.string.loading_details)
        forecastText.text = ""

        thread {
            try {
                val details = WeatherApi.fetchDetails(city.latitude, city.longitude)
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    currentText.text = buildString {
                        appendLine("Current: ${details.description}")
                        appendLine("Temperature: ${details.currentTemperature} C")
                        appendLine("Feels like: ${details.apparentTemperature} C")
                        appendLine("Humidity: ${details.humidity}%")
                        appendLine("Wind speed: ${details.windSpeed} km/h")
                    }
                    forecastText.text = details.forecast.joinToString(separator = "\n")
                }
            } catch (exception: Exception) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    currentText.text = getString(
                        R.string.network_error,
                        exception.localizedMessage ?: "error"
                    )
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val EXTRA_CITY = "extra_city"
    }
}
