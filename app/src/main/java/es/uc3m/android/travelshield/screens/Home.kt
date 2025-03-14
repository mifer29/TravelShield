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
import es.uc3m.android.travelshield.NavGraph

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // First row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CountryBox(
                countryName = "Australia",
                imageRes = R.drawable.australia,
                onClick = { navController.navigate(NavGraph.Country.route)}
            )
            CountryBox(
                countryName = "USA",
                imageRes = R.drawable.usa,
                onClick = { navController.navigate(NavGraph.Country.route)}
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Second row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CountryBox(
                countryName = "Thailand",
                imageRes = R.drawable.bangkok,
                onClick = { navController.navigate(NavGraph.Country.route)}
            )
            CountryBox(
                countryName = "Switzerland",
                imageRes = R.drawable.geneva,
                onClick = { navController.navigate(NavGraph.Country.route)}
            )
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController)
}
