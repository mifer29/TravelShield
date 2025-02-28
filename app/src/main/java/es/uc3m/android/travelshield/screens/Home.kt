package es.uc3m.android.travelshield.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.uc3m.android.travelshield.NavGraph

@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Australia")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate(NavGraph.Country.route) }) {
                Text(text = "Go to Country Screen")
            }
        }
    }
}
