package es.uc3m.android.travelshield.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import es.uc3m.android.travelshield.NavGraph
import es.uc3m.android.travelshield.R
import es.uc3m.android.travelshield.viewmodel.CountryReviewsViewModel
import es.uc3m.android.travelshield.viewmodel.LikeCountViewModel
import es.uc3m.android.travelshield.viewmodel.LikeViewModel
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.firestore.FirebaseFirestore
import es.uc3m.android.travelshield.notifications.NotificationHelper
import es.uc3m.android.travelshield.viewmodel.CountryViewModel

@Composable
fun CountryScreen(navController: NavController, countryId: String) {
    val context = LocalContext.current
    val likeCountViewModel: LikeCountViewModel = viewModel()
    val likeViewModel: LikeViewModel = remember { LikeViewModel(likeCountViewModel =likeCountViewModel) }
    val liked by likeViewModel.liked.collectAsState()

    val countryReviewsViewModel: CountryReviewsViewModel = viewModel()
    val countryReviews by countryReviewsViewModel.reviews.collectAsState()

    val countryViewModel: CountryViewModel = viewModel()
    val countryFlow = remember { countryViewModel.getCountryById(countryId) }
    val country by countryFlow.collectAsState()

    var abbreviation by remember { mutableStateOf<String?>(null) }

    val averageRating = countryReviews.map { it.rating }.average().takeIf { it.isFinite() } ?: 0.0
    val ratingCount = countryReviews.size

    LaunchedEffect(country) {
        country?.let {
            likeViewModel.loadLikeStatus(it.name.en)
            countryReviewsViewModel.fetchReviewsByCountry(it.name.en)

            // Fetch abbreviation from Firestore
            val lang = context.resources.configuration.locales[0].language
            val countryName = if (lang == "es") it.name.es else it.name.en

            val db = FirebaseFirestore.getInstance("travelshield-db")
            db.collection("countries")
                .whereEqualTo("name.${lang}", countryName)
                .get()
                .addOnSuccessListener { snapshot ->
                    val abbr = snapshot.documents.firstOrNull()?.getString("abbreviation")?.uppercase()
                    abbreviation = abbr
                }
                .addOnFailureListener { e ->
                    Log.e("CountryScreen", "Failed to fetch abbreviation: ${e.message}")
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(15.dp))

        country?.let {
            val lang = LocalContext.current.resources.configuration.locales[0].language
            val name = if (lang == "es") it.name.es else it.name.en

            HeaderSection(
                navController = navController,
                countryName = name,
                abbreviation = abbreviation,
                averageRating = averageRating,
                ratingCount = ratingCount,
                liked = liked,
                onLikeClick = { likeViewModel.toggleLike(it.name.en) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            CountryImage(imageUrl = it.imageUrl, countryName = name)

            Spacer(modifier = Modifier.height(30.dp))

            CategoryGrid(navController = navController, countryName = name)

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
            ) {
                Text(text = stringResource(R.string.go_back))
            }
        } ?: Text(stringResource(R.string.cargando_pais))
    }
}

@Composable
fun HeaderSection(
    navController: NavController,
    countryName: String,
    abbreviation: String?,
    averageRating: Double,
    ratingCount: Int,
    liked: Boolean,
    onLikeClick: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            abbreviation?.let {
                val flagUrl = "https://flagsapi.com/${it}/flat/64.png"
                Image(
                    painter = rememberAsyncImagePainter(flagUrl),
                    contentDescription = stringResource(R.string.country_flag, countryName),
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 8.dp)
                )
            }

            Column {
                Text(
                    text = countryName,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                RatingSection(
                    averageRating = averageRating,
                    ratingCount = ratingCount,
                    onClick = { navController.navigate(NavGraph.CountryReviews.createRoute(countryName)) }
                )
            }
        }

        IconButton(
            onClick = {
                val newLikedState = !liked // Predict the new state before toggling
                onLikeClick() // Actually toggles the state in ViewModel

                val notificationHelper = NotificationHelper(context)
                val message = if (newLikedState) {
                    context.getString(R.string.you_have_successfully_liked, countryName)
                } else {
                    context.getString(R.string.you_have_removed_your_like_from, countryName)
                }

                notificationHelper.showNotification(
                    "Like Updated",
                    message
                )
            }
        )
        {
            Icon(
                painter = painterResource(id = R.drawable.heart),
                contentDescription = stringResource(R.string.favorite),
                tint = if (liked) Color.Red else Color.Gray
            )
        }
    }
}


@Composable
fun RatingSection(
    averageRating: Double,
    ratingCount: Int,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = String.format("%.1f", averageRating),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(end = 4.dp)
        )
        repeat(averageRating.toInt()) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = stringResource(R.string.rating_star),
                tint = Color.Yellow,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "($ratingCount)",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun CountryImage(imageUrl: String?, countryName: String) {
    val painter = rememberAsyncImagePainter(
        model = imageUrl,
        fallback = painterResource(id = R.drawable.country_default),
        error = painterResource(id = R.drawable.country_default)
    )

    Image(
        painter = painter,
        contentDescription = countryName,
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun CategoryGrid(navController: NavController, countryName: String) {
    val categories = listOf(
        stringResource(R.string.general_info),
        stringResource(R.string.health),
        stringResource(R.string.visa),
        stringResource(R.string.security),
        stringResource(R.string.weather),
        stringResource(R.string.transport)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        categories.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { category ->
                    CategoryItem(name = category, navController = navController, countryName = countryName)
                }
            }
        }
    }
}

@Composable
fun CategoryItem(name: String, navController: NavController, countryName: String) {
    val route = when (name) {
        stringResource(R.string.general_info) -> NavGraph.GeneralInfo.createRoute(countryName)
        stringResource(R.string.health) -> NavGraph.Health.createRoute(countryName)
        stringResource(R.string.visa) -> NavGraph.Visa.createRoute(countryName)
        stringResource(R.string.security) -> NavGraph.Security.createRoute(countryName)
        stringResource(R.string.weather) -> NavGraph.News.createRoute(countryName)
        stringResource(R.string.transport) -> NavGraph.Transport.createRoute(countryName)
        else -> NavGraph.Home.route
    }

    val imageResId = when (name) {
        stringResource(R.string.general_info) -> R.drawable.categories_info
        stringResource(R.string.health) -> R.drawable.categories_hospital
        stringResource(R.string.visa) -> R.drawable.categories_visa
        stringResource(R.string.security) -> R.drawable.categories_security
        stringResource(R.string.weather) -> R.drawable.categories_weather
        stringResource(R.string.transport) -> R.drawable.categories_transport
        else -> R.drawable.categories_info
    }

    Button(
        onClick = { navController.navigate(route) },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.size(100.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Gray,
            contentColor = Color.White
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = stringResource(R.string.category_icon, name),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
