package es.uc3m.android.travelshield.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.runtime.remember

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Header Text
        Text(
            text = "Traveling information...",
            fontSize = 12.sp
        )

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
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

// **Category Grid Layout**
@Composable
fun CategoryGrid(navController: NavController, modifier: Modifier = Modifier) {
    val categories = listOf("General Info", "Health", "Visa", "Security", "News", "Transport")

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly // Distributes rows evenly
    ) {
        for (row in categories.chunked(3)) { // 3 items per row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (category in row) {
                    CategoryItem(name = category, navController = navController)
                }
            }
        }
    }
}

// **Category Item as a Button**
@Composable
fun CategoryItem(name: String, navController: NavController) {
    val route = when (name) {
        "General Info" -> NavGraph.GeneralInfo.route
        "Health" -> NavGraph.Health.route
        "Visa" -> NavGraph.Visa.route
        "Security" -> NavGraph.Security.route
        "News" -> NavGraph.News.route
        "Transport" -> NavGraph.Transport.route
        else -> NavGraph.Home.route // Fallback (puede cambiarse según lo necesario)
    }

    Button(
        onClick = { navController.navigate(route) }, // Navegar a la categoría específica
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
            Icon(
                painter = painterResource(id = R.drawable.heart), // Sustituir con el icono correcto
                contentDescription = "$name Icon",
                modifier = Modifier.size(40.dp),
                tint = Color.White
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
    CountryScreen(navController = navController, "Australia")
}