package es.uc3m.android.travelshield.screens.categories

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import es.uc3m.android.travelshield.R
import es.uc3m.android.travelshield.viewmodel.WeatherViewModel
import kotlinx.coroutines.tasks.await

@Composable
fun NewsScreen(navController: NavController, countryName: String) {
    val weatherViewModel: WeatherViewModel = viewModel()
    val weatherData by weatherViewModel.weatherData.collectAsState()

    var newsInfo by remember { mutableStateOf("Loading...") }
    var weatherAvailable by remember { mutableStateOf(false) }

    LaunchedEffect(countryName) {

        val coords = fetchLatLongFromFirebase(countryName)
        coords?.let { (lat, long) ->
            weatherViewModel.fetchWeather(lat, long)
            weatherAvailable = true
        } ?: run {
            weatherAvailable = true
        }
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.weather_for, countryName),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )



            Spacer(modifier = Modifier.height(16.dp))

            if (weatherAvailable && weatherData != null) {
                val temperature = weatherData?.temperature?.degrees
                val feelsLike = weatherData?.feelsLikeTemperature?.degrees
                val conditions = weatherData?.weatherCondition?.description?.text
                val humidity = weatherData?.relativeHumidity
                val windSpeed = weatherData?.wind?.speed?.value
                val windGust = weatherData?.wind?.gust?.value
                val windDirection = weatherData?.wind?.direction?.cardinal

                // Main weather info card
                if (temperature != null && conditions != null) {
                    Text(
                        text = stringResource(R.string.weather_in_capital_city),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .align(Alignment.Start)
                    )

                    // Temperature card
                    WeatherCard(
                        title = stringResource(R.string.temperature),
                        content = "$temperature°C",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Feels like card
                    WeatherCard(
                        title = stringResource(R.string.feels_like),
                        content = "$feelsLike°C",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Conditions card
                    WeatherCard(
                        title = stringResource(R.string.conditions),
                        content = conditions ?: "No condition available",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.weather_data_not_available),
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Additional weather details cards
                if (humidity != null) {
                    WeatherCard(
                        title = stringResource(R.string.humidity),
                        content = "$humidity%",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (windSpeed != null || windDirection != null) {
                    val speed = String.format("%.1f", windSpeed ?: 0.0)
                    val speedUnit = when (weatherData?.wind?.speed?.unit) {
                        "KILOMETERS_PER_HOUR" -> "km/h"
                        "MILES_PER_HOUR" -> "mph"
                        else -> ""
                    }
                    val direction = windDirection
                        ?.lowercase()
                        ?.split("_")
                        ?.joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                        ?: "Unknown"

                    // Wind speed and direction card
                    WeatherCard(
                        title = stringResource(R.string.wind),
                        content = "$speed $speedUnit at $direction",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    if (windGust != null) {
                        val gust = String.format("%.1f", windGust)
                        val gustUnit = when (weatherData?.wind?.gust?.unit) {
                            "KILOMETERS_PER_HOUR" -> "km/h"
                            "MILES_PER_HOUR" -> "mph"
                            else -> ""
                        }

                        // Wind gust card
                        WeatherCard(
                            title = stringResource(R.string.wind_gust),
                            content = "$gust $gustUnit",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.weather_data_not_available),
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.go_back))
            }
        }
    }
}

@Composable
fun NewsCard(title: String, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(text = content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun WeatherCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .shadow(4.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

suspend fun fetchNewsInfoFromFirebase(countryName: String): String {
    val db = FirebaseFirestore.getInstance("travelshield-db")
    return try {
        val doc = db.collection("countries").whereEqualTo("name", countryName).get().await().documents.firstOrNull()
        doc?.getString("newsInfo") ?: "No news info available"
    } catch (e: Exception) {
        "Failed to load news info"
    }
}

suspend fun fetchLatLongFromFirebase(countryName: String): Pair<Double, Double>? {
    val db = FirebaseFirestore.getInstance("travelshield-db")
    val language = java.util.Locale.getDefault().language  // "en" or "es"

    return try {
        val querySnapshot = db.collection("countries").get().await()
        //println("Total countries fetched: ${querySnapshot.size()}")

        val matchingDoc = querySnapshot.documents.firstOrNull { doc ->
            val nameMap = doc.get("name") as? Map<*, *>
            val localizedName = nameMap?.get(language) as? String
            //println("Checking country: $localizedName against $countryName")
            localizedName == countryName
        }

        val genInfo = matchingDoc?.get("genInfo") as? Map<*, *>
        val lat = genInfo?.get("lat") as? Double
        val long = genInfo?.get("long") as? Double
        //println("Extracted coordinates: lat=$lat, long=$long")

        if (lat != null && long != null) Pair(lat, long) else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Preview(showBackground = true)
@Composable
fun NewsScreenPreview() {
    val navController = rememberNavController()
    NewsScreen(navController = navController, countryName = "USA")
}
