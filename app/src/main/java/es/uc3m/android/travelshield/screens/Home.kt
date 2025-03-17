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

@Composable
fun HomeScreen(navController: NavController) {
    val countries = listOf("Australia", "USA", "Thailand", "Switzerland")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        for (row in countries.chunked(2)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
            Spacer(modifier = Modifier.height(16.dp))
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
    return when (countryName) {
        "Australia" -> R.drawable.country_australia
        "USA" -> R.drawable.country_usa
        "Thailand" -> R.drawable.country_thailand
        "Switzerland" -> R.drawable.country_switzerland
        else -> R.drawable.country_australia // Fallback
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController)
}