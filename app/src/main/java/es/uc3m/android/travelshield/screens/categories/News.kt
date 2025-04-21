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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import es.uc3m.android.travelshield.viewmodel.WeatherViewModel
import kotlinx.coroutines.tasks.await

@Composable
fun NewsScreen(navController: NavController, countryName: String) {
    val weatherViewModel: WeatherViewModel = viewModel()
    val weatherData by weatherViewModel.weatherData.collectAsState()

    var newsInfo by remember { mutableStateOf("Loading...") }

    LaunchedEffect(countryName) {
        newsInfo = fetchNewsInfoFromFirebase(countryName)
        weatherViewModel.fetchWeather(countryName)
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
                text = "News for $countryName",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            NewsCard("Latest News", newsInfo)

            Spacer(modifier = Modifier.height(16.dp))

            weatherData?.let {
                val temperature = it.currentWeather?.temperature
                val conditions = it.currentWeather?.description ?: "No description"

                if (temperature != null) {
                    Text(
                        text = "Weather: $temperature°C, $conditions",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                } else {
                    Text(
                        text = "Weather data not available",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text("Go Back")
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

suspend fun fetchNewsInfoFromFirebase(countryName: String): String {
    val db = FirebaseFirestore.getInstance("travelshield-db")
    return try {
        val doc = db.collection("countries").document(countryName).get().await()
        doc.getString("newsInfo") ?: "No news info available"
    } catch (e: Exception) {
        "Failed to load news info"
    }
}

@Preview(showBackground = true)
@Composable
fun NewsScreenPreview() {
    val navController = rememberNavController()
    NewsScreen(navController = navController, countryName = "USA")
}
