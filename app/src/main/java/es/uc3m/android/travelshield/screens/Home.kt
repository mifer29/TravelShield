package es.uc3m.android.travelshield.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import es.uc3m.android.travelshield.R
import es.uc3m.android.travelshield.viewmodel.CountryDoc
import es.uc3m.android.travelshield.viewmodel.CountryViewModel
import es.uc3m.android.travelshield.viewmodel.LikeViewModel
import es.uc3m.android.travelshield.viewmodel.CountryReviewsViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    countryViewModel: CountryViewModel,
    likeViewModel: LikeViewModel,
    countryReviewsViewModel: CountryReviewsViewModel
) {
    val countries by countryViewModel.countries.collectAsState()
    val likedCountries by likeViewModel.likedCountries.collectAsState()
    val reviews by countryReviewsViewModel.reviews.collectAsState()
    val searchQuery = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        likeViewModel.loadLikedCountries()
        countryReviewsViewModel.fetchAllReviews()
    }

    val lang = LocalContext.current.resources.configuration.locales[0].language

    val likedCountryDocs = countries.filter {
        val name = if (lang == "es") it.name.es else it.name.en
        name in likedCountries
    }

    val reviewsByCountry = reviews.groupingBy { it.country }.eachCount()
    val topReviewedCountries = reviewsByCountry.entries
        .sortedByDescending { it.value }
        .map { it.key }
        .take(5)

    val trendingCountries = countries.filter { topReviewedCountries.contains(it.name.en) }
    val extraCountries = countries
        .filterNot { it in trendingCountries }
        .shuffled()
        .take(5 - trendingCountries.size)

    val finalTrending = trendingCountries + extraCountries

    val filteredCountries = countries.filter {
        val localizedName = if (lang == "es") it.name.es else it.name.en
        localizedName.contains(searchQuery.value.trim(), ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        TopSection(searchQuery)

        Spacer(modifier = Modifier.height(16.dp))

        // Sección de resultados del buscador
        if (searchQuery.value.isNotBlank()) {
            Text(
                text = stringResource(R.string.search_results),
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (filteredCountries.isEmpty()) {
                Text(text = stringResource(R.string.no_destinations))
            } else {
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(filteredCountries) { country ->
                        CountryCard(country, navController)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Trending
        Text(
            text = stringResource(R.string.trending_destinations),
            fontSize = 24.sp,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (finalTrending.isEmpty()) {
            Text(text = stringResource(R.string.no_destinations))
        } else {
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(finalTrending) { country ->
                    CountryCard(country, navController)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Liked
        Text(
            text = stringResource(R.string.liked_destinations),
            fontSize = 24.sp,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (likedCountryDocs.isEmpty()) {
            Text(text = stringResource(R.string.you_havent_liked_any_countries_yet))
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
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.welcome_to_travelshield),
                fontSize = 16.sp,
                fontWeight = FontWeight.Light
            )
            Icon(
                imageVector = Icons.Filled.Flight,
                contentDescription = stringResource(R.string.airplane_icon)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.where_to_next),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        SearchBar(searchQuery)
        Spacer(modifier = Modifier.height(25.dp))
    }
}

@Composable
fun SearchBar(searchQuery: MutableState<String>) {
    TextField(
        value = searchQuery.value,
        onValueChange = { searchQuery.value = it },
        placeholder = { Text(stringResource(R.string.search_for_your_new_adventure)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(R.string.search)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun CountryCard(country: CountryDoc, navController: NavController) {
    val lang = LocalContext.current.resources.configuration.locales[0].language
    val name = if (lang == "es") country.name.es else country.name.en

    Column(
        modifier = Modifier
            .width(200.dp)
            .padding(8.dp)
            .clickable {
                navController.navigate("country/${country.id}")
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = country.imageUrl),
            contentDescription = country.name.en,
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
