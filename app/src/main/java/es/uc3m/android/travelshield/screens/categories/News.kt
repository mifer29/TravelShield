package es.uc3m.android.travelshield.screens.categories

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun NewsScreen(navController: NavController, countryName: String) {
    var newsInfo by remember { mutableStateOf<String>("Loading...") }

    // Fetch news info from Firebase
    LaunchedEffect(countryName) {
        newsInfo = fetchNewsInfoFromFirebase(countryName)
    }

    // Remember scroll state for the News screen
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp) // Add padding around the screen
                .fillMaxWidth()
                .verticalScroll(scrollState), // Make the column scrollable
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "News for $countryName",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Display the news in a card
            NewsCard("Latest News", newsInfo)

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Go Back")
            }
        }
    }
}

@Composable
fun NewsCard(title: String, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp) // Add vertical spacing between cards
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp) // Space between title and content
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// This is for handling news update in a asynchronous way
suspend fun fetchNewsInfoFromFirebase(countryName: String): String {
    val db = FirebaseFirestore.getInstance()
    return try {
        // Adjust the path to your Firebase Firestore collection
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
