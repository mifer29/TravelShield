package es.uc3m.android.travelshield.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import es.uc3m.android.travelshield.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import es.uc3m.android.travelshield.NavGraph

import androidx.lifecycle.viewmodel.compose.viewModel
import es.uc3m.android.travelshield.viewmodel.Review

@Composable
fun CountryScreen(navController: NavController, countryName: String) {
    val countryImageName = "country_${countryName.lowercase().replace(" ", "_")}"

    // Obtener ID de la imagen en drawable
    val imageResId = remember(countryImageName) {
        try {
            val resId = R.drawable::class.java.getField(countryImageName).getInt(null)
            resId
        } catch (e: Exception) {
            R.drawable.country_default // Imagen por defecto si no existe
        }
    }



    // Make the entire column scrollable by wrapping it with a Scrollable Column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Country Title + Favorite Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = countryName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            // Heart Icon (en drawable heart.png)
            IconButton(onClick = { /* Acción cuando se marca como favorito */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.heart),
                    contentDescription = "Favorite",
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Country Image
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "$countryName Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = "",
            onValueChange = { /* Manejar la búsqueda cuando la base de datos esté lista */ },
            placeholder = { Text("Search for information") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Categorías
        CategoryGrid(
            navController = navController,
            countryName = countryName,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Reviews Section
        Text(
            text = "Reviews",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Write Review Button
        Button(
            onClick = { navController.navigate(NavGraph.WriteReview.createRoute(countryName)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Write a Review", fontSize = 16.sp)
        }
    }
}

// **Review Item to display individual reviews**
@Composable
fun ReviewItemCountry(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = review.comment,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Rating: ${review.rating}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Reviewed by: ${review.userId}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}

// **Category Grid Layout**
@Composable
fun CategoryGrid(navController: NavController, countryName: String, modifier: Modifier = Modifier) {
    val categories = listOf("General Info", "Health", "Visa", "Security", "News", "Transport")

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp) // Adds spacing between rows
    ) {
        for (row in categories.chunked(3)) { // 3 items per row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (category in row) {
                    CategoryItem(name = category, navController = navController, countryName = countryName)
                }
            }
        }
    }
}

// **Category Item as a Button**
@Composable
fun CategoryItem(name: String, navController: NavController, countryName: String) {
    val route = when (name) {
        "General Info" -> NavGraph.GeneralInfo.createRoute(countryName)
        "Health" -> NavGraph.Health.createRoute(countryName)
        "Visa" -> NavGraph.Visa.createRoute(countryName)
        "Security" -> NavGraph.Security.createRoute(countryName)
        "News" -> NavGraph.News.createRoute(countryName)
        "Transport" -> NavGraph.Transport.createRoute(countryName)
        else -> NavGraph.Home.route // Fallback (puede cambiarse según lo necesario)
    }

    val imageResId = when (name) {
        "General Info" -> R.drawable.categories_info
        "Health" -> R.drawable.categories_hospital
        "Visa" -> R.drawable.categories_visa
        "Security" -> R.drawable.categories_security
        "News" -> R.drawable.categories_news
        "Transport" -> R.drawable.categories_transport
        else -> R.drawable.categories_info // Default to general info icon if something is missing
    }

    Button(
        onClick = {
            // Correctly navigate using the createRoute method
            navController.navigate(route)
        },
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

            Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCountryScreen() {
    val navController = rememberNavController() // Mock NavController for preview
    CountryScreen(navController = navController, countryName = "USA")
}
