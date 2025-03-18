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


@Composable
fun HomeScreen(navController: NavController) {
    // Here we get the country list from strings.xml and split it into a list, AVOIDING HARDCODED STRINGS
    // explanation: split with ',' delimiter. The map thing is to remove empty extra spaces
    val countries = stringResource(id = R.string.country_list).split(", ").map { it.trim() }

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

        for (row in countries.chunked(2)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { country ->
                    CountryBox(
                        countryName = country,
                        imageRes = getImageRes(country),
                        onClick = { navController.navigate("country/$country") }
                    )
                }
            }
        }
    }
}

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

fun getImageRes(countryName: String): Int {
    val resourceId = try {
        val resName = "country_${countryName.lowercase()}"
        // Searching for image with that name
        val resId = R.drawable::class.java.getDeclaredField(resName).getInt(null)
        resId
    } catch (e: Exception) {
        // If not country image found with that name, default image
        R.drawable.country_default
    }
    return resourceId
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController)
}
