package es.uc3m.android.travelshield.screens.categories

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import es.uc3m.android.travelshield.viewmodel.CountryViewModel
import androidx.navigation.compose.rememberNavController

@Composable
fun SecurityScreen(navController: NavController, countryName: String) {
    val countryViewModel: CountryViewModel = viewModel()

    // Observe the list of countries in the ViewModel
    val countries by countryViewModel.countries.collectAsState()

    // Find the country data matching the countryName
    val country = countries.find { it.name == countryName }

    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Security for $countryName",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (country != null) {
                // Display security-related information
                SecurityInfoCard("Common Scams", country.security.commonScams.joinToString())
                SecurityInfoCard("Crime Level", country.security.crimeLevel)
                SecurityInfoCard("Police Emergency Number", country.security.emergencyContacts.police.toString())
                SecurityInfoCard("Embassy Contact", country.security.emergencyContacts.embassy)
            } else {
                // Show loading or error state if country not found
                Text(text = "Country not found!")
            }

            // Emergency contact button
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val emergencyNumber = country?.security?.emergencyContacts?.police ?: "999"
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$emergencyNumber") // Adjust based on the country emergency number
                    }
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call Emergency",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Call Emergency")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Go Back")
            }
        }
    }
}

@Composable
fun SecurityInfoCard(title: String, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp) // Add vertical spacing between cards
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
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
fun SecurityScreenPreview() {
    val navController = rememberNavController()
    SecurityScreen(navController = navController, countryName = "USA")
}
