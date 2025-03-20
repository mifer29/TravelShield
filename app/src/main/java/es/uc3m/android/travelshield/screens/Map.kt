package es.uc3m.android.travelshield.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.ui.res.stringResource
import es.uc3m.android.travelshield.R

@Composable
fun MapScreen(navController: NavController) {
    // In the future it will hold map logic with locations.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(R.string.map_message))
    }
}
