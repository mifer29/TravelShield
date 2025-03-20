package es.uc3m.android.travelshield.screens.categories

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import es.uc3m.android.travelshield.R

@Composable
fun GeneralInfoScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.categories_general_info))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text(text = stringResource(R.string.go_back))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GeneralInfoScreenPreview() {
    val navController = rememberNavController()
    GeneralInfoScreen(navController = navController)
}
