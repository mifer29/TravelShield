package es.uc3m.android.travelshield.screens.categories

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import es.uc3m.android.travelshield.R
import es.uc3m.android.travelshield.viewmodel.CountryViewModel
import androidx.core.net.toUri

@Composable
fun TransportScreen(navController: NavController, countryName: String) {
    val countryViewModel: CountryViewModel = viewModel()

    // Observe the list of countries in the ViewModel
    val countries by countryViewModel.countries.collectAsState()

    // Find the country data matching the countryName
    val lang = LocalContext.current.resources.configuration.locales[0].language
    val country = countries.find {
        (if (lang == "es") it.name.es else it.name.en) == countryName
    }


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
                text = stringResource(R.string.transport_info_for, countryName),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (country != null) {
                // Display transport-related information in styled cards
                val publicTransport = if (lang == "es") country.transport.public.es else country.transport.public.en
                val transportApps = if (lang == "es") country.transport.apps.es else country.transport.apps.en
                val airportToCity = if (lang == "es") country.transport.airportToCity.es else country.transport.airportToCity.en

                TransportInfoCard(stringResource(R.string.public_transport), publicTransport)
                TransportInfoCard(stringResource(R.string.transport_apps), transportApps)
                TransportInfoCard(stringResource(R.string.airport_to_city), airportToCity)

            } else {
                // Show loading or error state if country not found
                Text(text = "Country not found!")
            }

            // Button for checking transport info in Maps
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val lat = country!!.genInfo.lat
                val lon = country.genInfo.long
                val geoUri = "geo:$lat,$lon?q=$lat,$lon(${country.name.en})"
                val intent = Intent(Intent.ACTION_VIEW, geoUri.toUri())
                context.startActivity(intent)
            }) {
                Text(text = stringResource(R.string.view_transport_on_maps))
            }

            Button(onClick = { navController.popBackStack() }) {
                Text(text = stringResource(R.string.go_back))
            }
        }
    }
}

@Composable
fun TransportInfoCard(title: String, content: String) {
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
fun TransportScreenPreview() {
    val navController = rememberNavController()
    TransportScreen(navController = navController, countryName = "USA")
}
