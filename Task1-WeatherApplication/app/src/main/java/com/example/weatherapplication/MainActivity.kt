package com.example.weatherapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var cityInput: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView
    private lateinit var listView: ListView
    private lateinit var adapter: CityWeatherAdapter

    private val cities = mutableListOf(
        CityWeatherItem("Minsk", "Belarus", 53.9006, 27.5590),
        CityWeatherItem("Vitebsk", "Belarus", 55.1904, 30.2049)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityInput = findViewById(R.id.cityInput)
        progressBar = findViewById(R.id.progressBar)
        statusText = findViewById(R.id.statusText)
        listView = findViewById(R.id.cityListView)
        val addButton = findViewById<Button>(R.id.addCityButton)
        val refreshButton = findViewById<Button>(R.id.refreshButton)

        adapter = CityWeatherAdapter(this, cities)
        adapter = CityWeatherAdapter(this, cities)
        listView.adapter = adapter
        addButton.setOnClickListener { addCustomCity() }
        refreshButton.setOnClickListener { refreshAllCities() }
        listView.setOnItemClickListener { _, _, position, _ ->
            openDetails(cities[position])
        }

        refreshAllCities()
    }

    private fun addCustomCity() {
        val query = cityInput.text.toString().trim()
        if (query.isEmpty()) {
            Toast.makeText(this, R.string.enter_city, Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true, getString(R.string.searching_city))
        thread {
            try {
                val city = WeatherApi.findCity(query)
                runOnUiThread {
                    if (city == null) {
                        setLoading(false, getString(R.string.city_not_found))
                        return@runOnUiThread
                    }

                    val exists = cities.any {
                        it.name.equals(city.name, ignoreCase = true) &&
                            it.country.equals(city.country, ignoreCase = true)
                    }
                    if (exists) {
                        setLoading(false, getString(R.string.city_already_added))
                        return@runOnUiThread
                    }

                    cities.add(city)
                    adapter.notifyDataSetChanged()
                    cityInput.text.clear()
                    setLoading(false, getString(R.string.city_added, city.name))
                    refreshCity(city)
                }
            } catch (exception: Exception) {
                runOnUiThread {
                    setLoading(
                        false,
                        getString(R.string.network_error, exception.localizedMessage ?: "error")
                    )
                }
            }
        }
    }

    private fun refreshAllCities() {
        setLoading(true, getString(R.string.loading_weather))
        val remaining = intArrayOf(cities.size)

        cities.forEach { city ->
            thread {
                try {
                    val current = WeatherApi.fetchCurrent(city.latitude, city.longitude)
                    synchronized(city) {
                        city.currentTemperature = "${current.temperature} C"
                        city.currentSummary = current.description
                    }
                } catch (_: Exception) {
                    synchronized(city) {
                        city.currentTemperature = "--"
                        city.currentSummary = getString(R.string.unavailable)
                    }
                } finally {
                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                        remaining[0] -= 1
                        if (remaining[0] == 0) {
                            setLoading(false, getString(R.string.tap_city_for_details))
                        }
                    }
                }
            }
        }
    }

    private fun refreshCity(city: CityWeatherItem) {
        thread {
            try {
                val current = WeatherApi.fetchCurrent(city.latitude, city.longitude)
                synchronized(city) {
                    city.currentTemperature = "${current.temperature} C"
                    city.currentSummary = current.description
                }
                runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
            } catch (_: Exception) {
                runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun openDetails(city: CityWeatherItem) {
        val intent = Intent(this, WeatherDetailsActivity::class.java).apply {
            putExtra(WeatherDetailsActivity.EXTRA_CITY, city)
        }
        startActivity(intent)
    }

    private fun setLoading(isLoading: Boolean, message: String) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        statusText.text = message
    }
}
