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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext

@Composable
fun CountryScreen(navController: NavController, countryName: String) {

    val context = LocalContext.current

    val imageResId = remember(countryName) {
        val imageName = "country_${countryName.lowercase().replace(" ", "_")}"
        context.resources.getIdentifier(imageName, "drawable", context.packageName)
            .takeIf { it != 0 } ?: R.drawable.country_default
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
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

            // Heart Icon
            IconButton(onClick = { /* In the future we'll have here like logic */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.heart),
                    contentDescription = stringResource(R.string.favourite_icon),
                    tint = Color.Gray
                )
            }

        }

        Spacer(modifier = Modifier.height(8.dp))

        // Country Image
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "$countryName " + stringResource(R.string.image),
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
            onValueChange = { /* In the future we'll have here search logic */ },
            placeholder = { Text(stringResource(R.string.search_bar)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_icon)
                )
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


// Category Grid Layout
@Composable
fun CategoryGrid(navController: NavController, modifier: Modifier = Modifier) {
    val categories = listOf(
        stringResource(R.string.categories_general_info),
        stringResource(R.string.categories_health),
        stringResource(R.string.categories_visa),
        stringResource(R.string.categories_security),
        stringResource(R.string.categories_news),
        stringResource(R.string.categories_transport)
    )

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

// Category Item as a Button
@Composable
fun CategoryItem(name: String, navController: NavController) {
    val context = LocalContext.current

    // Mapping of categories to their routes
    val categoryRoutes = mapOf(
        context.getString(R.string.categories_general_info) to NavGraph.GeneralInfo.route,
        context.getString(R.string.categories_health) to NavGraph.Health.route,
        context.getString(R.string.categories_visa) to NavGraph.Visa.route,
        context.getString(R.string.categories_security) to NavGraph.Security.route,
        context.getString(R.string.categories_news) to NavGraph.News.route,
        context.getString(R.string.categories_transport) to NavGraph.Transport.route
    )

    // Mapping of the categories to their images
    val categoryImages = mapOf(
        context.getString(R.string.categories_general_info) to R.drawable.categories_info,
        context.getString(R.string.categories_health) to R.drawable.categories_hospital,
        context.getString(R.string.categories_visa) to R.drawable.categories_visa,
        context.getString(R.string.categories_security) to R.drawable.categories_security,
        context.getString(R.string.categories_news) to R.drawable.categories_news,
        context.getString(R.string.categories_transport) to R.drawable.categories_transport
    )

    val route = categoryRoutes[name] ?: NavGraph.Home.route
    val imageResId = categoryImages[name] ?: R.drawable.categories_info // Default

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
                contentDescription = name,
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
    CountryScreen(navController = navController, stringResource(R.string.usa))
}
