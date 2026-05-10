package com.example.weatherapplication.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.weatherapplication.PlatformKind
import com.example.weatherapplication.data.CityWeather
import com.example.weatherapplication.data.WeatherCondition
import com.example.weatherapplication.localization.conditionLabel
import com.example.weatherapplication.localization.currentStrings

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    state: WeatherUiState,
    platform: PlatformKind,
    onSearchQueryChange: (String) -> Unit,
    onAddCity: () -> Unit,
    onRefresh: () -> Unit,
    onSelectCity: (Int) -> Unit
) {
    val strings = currentStrings()
    val spacing = 12.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        Header(platform = platform, title = strings.appName)

        if (state.statusMessage != null) {
            Text(text = state.statusMessage, color = MaterialTheme.colorScheme.primary)
        }

        SearchRow(
            platform = platform,
            query = state.searchQuery,
            onQueryChange = onSearchQueryChange,
            onAddCity = onAddCity,
            onRefresh = onRefresh
        )

        if (platform == PlatformKind.IOS && state.cities.isNotEmpty()) {
            CitySegmentedControl(
                cities = state.cities,
                selectedIndex = state.selectedCityIndex,
                onSelect = onSelectCity
            )
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val maxWidth = maxWidth
            val columns = when {
                platform != PlatformKind.Web -> 1
                maxWidth < 600.dp -> 1
                maxWidth < 1000.dp -> 2
                else -> 3
            }

            val visibleCities = if (platform == PlatformKind.IOS) {
                listOfNotNull(state.cities.getOrNull(state.selectedCityIndex))
            } else {
                state.cities
            }

            if (platform == PlatformKind.Web) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    verticalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    items(visibleCities.size) { index ->
                        WeatherCard(
                            entry = visibleCities[index],
                            platform = platform,
                            onClick = { onSelectCity(index) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    itemsIndexed(visibleCities) { index, entry ->
                        WeatherCard(
                            entry = entry,
                            platform = platform,
                            onClick = { onSelectCity(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(platform: PlatformKind, title: String) {
    val style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
    if (platform == PlatformKind.IOS) {
        Text(text = title, style = style)
    } else {
        Text(text = title, style = style)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchRow(
    platform: PlatformKind,
    query: String,
    onQueryChange: (String) -> Unit,
    onAddCity: () -> Unit,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchInput(
            platform = platform,
            query = query,
            onQueryChange = onQueryChange,
            modifier = Modifier.weight(1f)
        )

        when (platform) {
            PlatformKind.IOS -> TextButton(
                onClick = onAddCity,
                modifier = Modifier.testTag("add_button")
            ) {
                Text(text = currentStrings().addCity)
            }
            PlatformKind.Desktop -> OutlinedButton(
                onClick = onAddCity,
                modifier = Modifier.testTag("add_button")
            ) {
                Text(text = currentStrings().addCity)
            }
            else -> Button(
                onClick = onAddCity,
                modifier = Modifier.testTag("add_button")
            ) {
                Text(text = currentStrings().addCity)
            }
        }

        IconButton(onClick = onRefresh, modifier = Modifier.testTag("refresh_button")) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = currentStrings().refresh)
        }
    }
}

@Composable
private fun SearchInput(
    platform: PlatformKind,
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = currentStrings()
    val taggedModifier = modifier.testTag("search_input")
    when (platform) {
        PlatformKind.IOS -> IosSearchBar(
            query = query,
            onQueryChange = onQueryChange,
            hint = strings.searchHint,
            modifier = taggedModifier
        )
        PlatformKind.Desktop -> OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(text = strings.searchHint) },
            modifier = taggedModifier,
            shape = RectangleShape
        )
        else -> TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(text = strings.searchHint) },
            modifier = taggedModifier
        )
    }
}

@Composable
private fun IosSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Surface(
        modifier = modifier,
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            if (query.isEmpty()) {
                Text(text = hint, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@Composable
private fun CitySegmentedControl(
    cities: List<CityWeather>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        cities.forEachIndexed { index, entry ->
            val selected = index == selectedIndex
            val background = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
            val contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(background)
                    .clickable { onSelect(index) }
                    .padding(vertical = 8.dp, horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entry.city.name,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun WeatherCard(entry: CityWeather, platform: PlatformKind, onClick: () -> Unit) {
    val strings = currentStrings()
    val weather = entry.weather
    val shape: Shape = when (platform) {
        PlatformKind.Android -> RoundedCornerShape(16.dp)
        PlatformKind.IOS -> RoundedCornerShape(14.dp)
        PlatformKind.Desktop -> RectangleShape
        PlatformKind.Web -> RoundedCornerShape(16.dp)
    }

    val elevation = when (platform) {
        PlatformKind.Android -> CardDefaults.cardElevation(defaultElevation = 8.dp)
        PlatformKind.IOS -> CardDefaults.cardElevation(defaultElevation = 0.dp)
        PlatformKind.Desktop -> CardDefaults.cardElevation(defaultElevation = 0.dp)
        PlatformKind.Web -> CardDefaults.cardElevation(defaultElevation = 6.dp)
    }

    val border = when (platform) {
        PlatformKind.Desktop -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        PlatformKind.IOS -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        else -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onClick() }
            .testTag("city_card_${entry.city.name}"),
        shape = shape,
        elevation = elevation,
        border = border
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "${entry.city.name}, ${entry.city.country}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (weather != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = conditionIcon(weather.condition),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = conditionLabel(weather.condition, strings))
                }
                Text(text = "${strings.temperature}: ${weather.temperatureC} C")
                Text(text = "${strings.humidity}: ${weather.humidityPercent}%")
                Text(text = "${strings.windSpeed}: ${weather.windSpeedKmh} km/h")

                if (entry.isFromCache) {
                    val label = if (entry.isStale) strings.staleCache else strings.cachedData
                    Text(text = label, color = MaterialTheme.colorScheme.tertiary)
                }
            } else {
                Text(text = strings.loadingWeather)
            }
        }
    }
}

@Composable
private fun conditionIcon(condition: WeatherCondition) = when (condition) {
    WeatherCondition.Clear -> Icons.Default.WbSunny
    WeatherCondition.MainlyClear -> Icons.Default.WbSunny
    WeatherCondition.Cloudy -> Icons.Default.Cloud
    WeatherCondition.Fog -> Icons.Default.Cloud
    WeatherCondition.Drizzle -> Icons.Default.Grain
    WeatherCondition.Rain -> Icons.Default.Grain
    WeatherCondition.Snow -> Icons.Default.AcUnit
    WeatherCondition.Thunder -> Icons.Default.Thunderstorm
}
