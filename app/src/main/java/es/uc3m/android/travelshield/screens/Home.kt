package es.uc3m.android.travelshield.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import es.uc3m.android.travelshield.R
import es.uc3m.android.travelshield.viewmodel.CountryViewModel
import es.uc3m.android.travelshield.viewmodel.CountryDoc

@Composable
fun HomeScreen(navController: NavController, viewModel: CountryViewModel) {
    val countries by viewModel.countries.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopSection()
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Trending Destinations",
            fontSize = 24.sp,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(countries) { country ->
                CountryCard(country, navController)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Popular Destinations",
            fontSize = 24.sp,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(countries) { country ->
                CountryCard(country, navController)
            }
        }

        // FOR DEV USE! UNCOMMENT FOR MASSIVE COUNTRY UPLOAD
        //Spacer(modifier = Modifier.height(24.dp))
        //Button(onClick = {
        //    navController.navigate("upload_countries")
        //}) {
        //    Text("Upload Countries")
        //}
    }
}

@Composable
fun TopSection() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Welcome to TravelShield!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Light
            )
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications"
            )
        }
        Text(
            text = "Where to next?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        SearchBar()
    }
}

@Composable
fun SearchBar() {
    TextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Search for your new adventure") },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Search")
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun CountryCard(country: CountryDoc, navController: NavController) {
    val imageResId = getCountryImageResId(country.name)

    Column(
        modifier = Modifier
            .width(200.dp)
            .padding(8.dp)
            .clickable { navController.navigate("Country/${country.name}") },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = country.name,
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = country.name,
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


// This function should be in an auxiliary functions file
fun getCountryImageResId(countryName: String): Int {
    val imageName = "country_${countryName.lowercase().replace(" ", "_")}"
    return try {
        R.drawable::class.java.getField(imageName).getInt(null)
    } catch (e: Exception) {
        R.drawable.country_default
    }
}
