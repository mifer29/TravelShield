package es.uc3m.android.travelshield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import es.uc3m.android.travelshield.screens.CategoriesScreen
import es.uc3m.android.travelshield.screens.CountryScreen
import es.uc3m.android.travelshield.screens.HomeScreen
import es.uc3m.android.travelshield.screens.MapScreen
import es.uc3m.android.travelshield.screens.ProfileScreen
import es.uc3m.android.travelshield.screens.LoginScreen
import es.uc3m.android.travelshield.screens.SettingsScreen
import es.uc3m.android.travelshield.screens.categories.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TravelShieldApp()
        }
    }
}

@Composable
fun TravelShieldApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        NavigationGraph(navController, Modifier.padding(paddingValues))
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavGraph.Home,
        NavGraph.Map,
        NavGraph.Profile
    )
    var selectedItem by remember { mutableStateOf(0) }

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedItem,
                onClick = {
                    selectedItem = index
                    navController.navigate(item.route) {
                        popUpTo(NavGraph.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                label = { Text(text = item.route.replaceFirstChar { it.uppercase() }) },
                icon = {
                    when (index) {
                        0 -> Icon(imageVector = Icons.Default.Home, contentDescription = item.route)
                        1 -> Icon(imageVector = Icons.Default.Place, contentDescription = item.route)
                        2 -> Icon(imageVector = Icons.Default.Person, contentDescription = item.route)
                        else -> Icon(imageVector = Icons.Default.Home, contentDescription = item.route)
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = NavGraph.Home.route,
        modifier = modifier
    ) {
        // Pantallas generales
        composable(NavGraph.Home.route) { HomeScreen(navController) }
        composable(NavGraph.Map.route) { MapScreen(navController) }
        composable(NavGraph.Profile.route) { ProfileScreen(navController) }
        composable(NavGraph.Country.route) { CountryScreen(navController) }
        composable(NavGraph.Login.route) { LoginScreen(navController) }
        composable(NavGraph.Settings.route) { SettingsScreen(navController) }

        // Pantallas de categorías
        composable(NavGraph.GeneralInfo.route) { GeneralInfoScreen(navController) }
        composable(NavGraph.Health.route) { HealthScreen(navController) }
        composable(NavGraph.News.route) { NewsScreen(navController) }
        composable(NavGraph.Security.route) { SecurityScreen(navController) }
        composable(NavGraph.Transport.route) { TranportScreen(navController) }
        composable(NavGraph.Visa.route) { VisaScreen(navController) }
    }
}

@Preview(showBackground = true)
@Composable
fun TravelShieldAppPreview() {
    TravelShieldApp()
}
