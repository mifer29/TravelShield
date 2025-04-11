package es.uc3m.android.travelshield.screens.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import es.uc3m.android.travelshield.viewmodel.CountryViewModel

@Composable
fun VisaScreen(navController: NavController, countryName: String) {
    val countryViewModel: CountryViewModel = viewModel()

    // Observe the list of countries in the ViewModel
    val countries by countryViewModel.countries.collectAsState()

    // Find the country data matching the countryName
    val country = countries.find { it.name == countryName }

    // Remember scroll state
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (country != null) {
            Column(
                modifier = Modifier
                    .padding(16.dp) // Add padding around the screen
                    .fillMaxWidth()
                    .verticalScroll(scrollState), // Make the column scrollable
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Visa Info for ${country.name}",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Create a Card to display each section of the visa info
                VisaCard("Visa Required", if (country.visa.required) "Yes" else "No")
                VisaCard("Visa Duration", country.visa.duration)
                VisaCard("Embassy Contact", country.visa.embassy)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(text = "Go Back")
                }
            }
        } else {
            // Show loading or error state if country not found
            Text(text = "Country not found!")
        }
    }
}

@Composable
fun VisaCard(title: String, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp) // Add vertical spacing between cards
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
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


@Preview(showBackground = true)
@Composable
fun VisaScreenPreview() {
    val navController = rememberNavController()
    VisaScreen(navController = navController, countryName = "USA")
}
