package es.uc3m.android.travelshield.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import es.uc3m.android.travelshield.viewmodel.LikeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    countryViewModel: CountryViewModel,
    likeViewModel: LikeViewModel
) {
    val countries by countryViewModel.countries.collectAsState()
    val likedCountries by likeViewModel.likedCountries.collectAsState()
    val searchQuery = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        likeViewModel.loadLikedCountries()
    }

    val filteredCountries = countries.filter {
        it.name.contains(searchQuery.value.trim(), ignoreCase = true)
    }

    val likedCountryDocs = countries.filter { it.name in likedCountries }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopSection(searchQuery)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Trending Destinations",
            fontSize = 24.sp,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (filteredCountries.isEmpty()) {
            Text("No destinations match your search.")
        } else {
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(filteredCountries) { country ->
                    CountryCard(country, navController)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Liked Destinations",
            fontSize = 24.sp,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (likedCountryDocs.isEmpty()) {
            Text("You haven't liked any countries yet.")
        } else {
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(likedCountryDocs) { country ->
                    CountryCard(country, navController)
                }
            }
        }
    }
}

@Composable
fun TopSection(searchQuery: MutableState<String>) {
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
        SearchBar(searchQuery)
    }
}

@Composable
fun SearchBar(searchQuery: MutableState<String>) {
    TextField(
        value = searchQuery.value,
        onValueChange = { searchQuery.value = it },
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
    // FOR DEV USE! UNCOMMENT FOR MASSIVE COUNTRY UPLOAD
    //Spacer(modifier = Modifier.height(24.dp))
    //Button(onClick = {
    //    navController.navigate("upload_countries")
    //}) {
    //    Text("Upload Countries")
    //}
}

fun getCountryImageResId(countryName: String): Int {
    val imageName = "country_${countryName.lowercase().replace(" ", "_")}"
    return try {
        R.drawable::class.java.getField(imageName).getInt(null)
    } catch (e: Exception) {
        R.drawable.country_default
    }
}
