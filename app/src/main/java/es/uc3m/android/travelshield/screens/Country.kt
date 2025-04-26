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

@Composable
fun CountryScreen(navController: NavController, countryName: String) {
    val countryImageName = "country_${countryName.lowercase().replace(" ", "_")}"
    val imageResId = remember(countryImageName) {
        try {
            R.drawable::class.java.getField(countryImageName).getInt(null)
        } catch (e: Exception) {
            R.drawable.country_default
        }
    }

    val likeCountViewModel: LikeCountViewModel = viewModel()
    val likeViewModel: LikeViewModel = remember { LikeViewModel(likeCountViewModel) }
    val liked by likeViewModel.liked.collectAsState()

    val countryReviewsViewModel: CountryReviewsViewModel = viewModel()
    val countryReviews by countryReviewsViewModel.reviews.collectAsState()

    LaunchedEffect(countryName) {
        likeViewModel.loadLikeStatus(countryName)
        countryReviewsViewModel.fetchReviewsByCountry(countryName)
    }

    val averageRating = countryReviews.map { it.rating }.average().takeIf { it.isFinite() } ?: 0.0
    val ratingCount = countryReviews.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(15.dp))

        HeaderSection(
            navController = navController,
            countryName = countryName,
            averageRating = averageRating,
            ratingCount = ratingCount,
            liked = liked,
            onLikeClick = { likeViewModel.toggleLike(countryName) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        CountryImage(imageResId = imageResId, countryName = countryName)

        Spacer(modifier = Modifier.height(30.dp))

        CategoryGrid(navController = navController, countryName = countryName)

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun HeaderSection(
    navController: NavController,
    countryName: String,
    averageRating: Double,
    ratingCount: Int,
    liked: Boolean,
    onLikeClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val flagUrl = "https://flagsapi.com/${countryName.take(2).uppercase()}/flat/64.png"
            Image(
                painter = rememberAsyncImagePainter(flagUrl),
                contentDescription = "$countryName Flag",
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp)
            )
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

        IconButton(onClick = onLikeClick) {
            Icon(
                painter = painterResource(id = R.drawable.heart),
                contentDescription = "Favorite",
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
                contentDescription = "Star",
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
fun CountryImage(imageResId: Int, countryName: String) {
    Image(
        painter = painterResource(id = imageResId),
        contentDescription = "$countryName Image",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp)),
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
        stringResource(R.string.news),
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
        stringResource(R.string.news) -> NavGraph.News.createRoute(countryName)
        stringResource(R.string.transport) -> NavGraph.Transport.createRoute(countryName)
        else -> NavGraph.Home.route
    }

    val imageResId = when (name) {
        stringResource(R.string.general_info) -> R.drawable.categories_info
        stringResource(R.string.health) -> R.drawable.categories_hospital
        stringResource(R.string.visa) -> R.drawable.categories_visa
        stringResource(R.string.security) -> R.drawable.categories_security
        stringResource(R.string.news) -> R.drawable.categories_news
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
                contentDescription = "$name Icon",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
