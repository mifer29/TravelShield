package es.uc3m.android.travelshield.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import es.uc3m.android.travelshield.R
import es.uc3m.android.travelshield.viewmodel.CountryViewModel
import es.uc3m.android.travelshield.viewmodel.CountryDoc
import androidx.compose.runtime.getValue




@Composable
fun HomeScreen(navController: NavController, viewModel: CountryViewModel) {
    // Collecting the countries state from the ViewModel
    val countries by viewModel.countries.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Trending Countries",
            fontSize = 28.sp,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        // Display the countries in a lazy column
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(countries) { country ->
                // Pass the navController to the CountryItem
                CountryItem(country = country, navController = navController)
            }
        }
    }
}

@Composable
fun CountryItem(country: CountryDoc, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Country Name
        Text(
            text = "Country: ${country.Name}", // Keep this one
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyLarge
        )


        // Vaccine Information
        Text(
            text = "Vaccine: ${country.Vaccine}",
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodyMedium
        )

    }
}






