package es.uc3m.android.travelshield.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import es.uc3m.android.travelshield.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext

@Composable
fun HomeScreen(navController: NavController) {

    val context = LocalContext.current
    val resources = context.resources

    // Define the list of countries to display
    val countries = listOf(
        stringResource(R.string.australia),
        stringResource(R.string.usa),
        stringResource(R.string.thailand),
        stringResource(R.string.switzerland)
    )

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

        // Create rows of countries, chunked to fit 2 countries per row
        for (row in countries.chunked(2)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { country ->
                    val imageName = "country_${country.lowercase()}"
                    val imageRes = resources.getIdentifier(imageName, "drawable", context.packageName)
                        .takeIf { it != 0 } ?: R.drawable.country_default

                    CountryBox(
                        countryName = country,
                        imageRes = imageRes,
                        onClick = { navController.navigate("country/$country") }
                    )
                }
            }
        }
    }
}


// A composable that displays a box with an image and the country name
@Composable
fun CountryBox(countryName: String, imageRes: Int, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "$countryName image",
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = countryName, fontSize = 16.sp)
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController)
}
