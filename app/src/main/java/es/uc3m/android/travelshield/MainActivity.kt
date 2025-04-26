package es.uc3m.android.travelshield

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import es.uc3m.android.travelshield.viewmodel.CountryViewModel
import es.uc3m.android.travelshield.viewmodel.LikeViewModel
import es.uc3m.android.travelshield.screens.CountryScreen
import es.uc3m.android.travelshield.screens.CountryUploadScreen
import es.uc3m.android.travelshield.screens.HomeScreen
import es.uc3m.android.travelshield.screens.MapScreen
import es.uc3m.android.travelshield.screens.ProfileScreen
import es.uc3m.android.travelshield.screens.LoginScreen
import es.uc3m.android.travelshield.screens.SettingsScreen
import es.uc3m.android.travelshield.screens.SignUpScreen
import es.uc3m.android.travelshield.screens.WriteReviewScreen
import es.uc3m.android.travelshield.screens.categories.*
import es.uc3m.android.travelshield.ui.theme.TravelShieldTheme
import es.uc3m.android.travelshield.screens.TripsScreen
import es.uc3m.android.travelshield.screens.EditProfileScreen
import es.uc3m.android.travelshield.notifications.NotificationHelper
import es.uc3m.android.travelshield.viewmodel.SettingsViewModel
import es.uc3m.android.travelshield.screens.CountryReviewsScreen

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
    val context = LocalContext.current
    val notificationHelper = NotificationHelper(context)

    val settingsViewModel: SettingsViewModel = viewModel()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(
                context, context.getString(R.string.permission_denied), Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    TravelShieldTheme(darkTheme = isDarkMode, dynamicColor = false) { // Fuerza los colores definidos en color.kt
        val navController = rememberNavController()
        val currentRoute by navController.currentBackStackEntryFlow.collectAsState(
            initial = navController.currentBackStackEntry
        )

        Scaffold(
            bottomBar = {
                if (currentRoute?.destination?.route != NavGraph.Login.route &&
                    currentRoute?.destination?.route != NavGraph.SignUp.route
                ) {
                    BottomNavigationBar(navController)
                }
            }
        ) { paddingValues ->
            NavigationGraph(navController, Modifier.padding(paddingValues))
        }
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

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary // Fondo de la barra
    ) {
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
                label = {
                    Text(
                        text = when (item.route) {
                            NavGraph.Home.route -> stringResource(R.string.bottom_nav_home)
                            NavGraph.Map.route -> stringResource(R.string.bottom_nav_map)
                            NavGraph.Profile.route -> stringResource(R.string.bottom_nav_profile)
                            else -> item.route
                        }
                    )
                }
                ,
                icon = {
                    when (index) {
                        0 -> Icon(imageVector = Icons.Default.Home, contentDescription = item.route)
                        1 -> Icon(imageVector = Icons.Default.Place, contentDescription = item.route)
                        2 -> Icon(imageVector = Icons.Default.Person, contentDescription = item.route)
                        else -> Icon(imageVector = Icons.Default.Home, contentDescription = item.route)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.secondary // Fondo bajo el icono/texto seleccionado
                )
            )
        }
    }

}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier) {
    val countryViewModel: CountryViewModel = viewModel()
    val likeViewModel: LikeViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = NavGraph.Login.route,
        modifier = modifier
    ) {
        // General Screens
        composable(NavGraph.Home.route) { HomeScreen(navController, countryViewModel, likeViewModel) }
        composable(NavGraph.Map.route) { MapScreen(navController, countryViewModel) }
        composable(NavGraph.Profile.route) { ProfileScreen(navController) }

        // Country Screen
        composable("country/{countryName}") { backStackEntry ->
            val countryName = backStackEntry.arguments?.getString("countryName") ?: "Unknown"
            CountryScreen(navController, countryName)
        }

        // Login and SignUp Screens
        composable(NavGraph.Login.route) { LoginScreen(navController) }
        composable(NavGraph.SignUp.route) { SignUpScreen(navController) }
        composable(NavGraph.SettingsScreen.route) { SettingsScreen(navController) }

        // Category Screens (Updated routes with 'categories/' prefix)
        composable("categories/general_info/{countryName}") { backStackEntry ->
            val countryName = backStackEntry.arguments?.getString("countryName") ?: "Unknown"
            GeneralInfoScreen(navController, countryName)
        }
        composable("categories/health/{countryName}") { backStackEntry ->
            val countryName = backStackEntry.arguments?.getString("countryName") ?: "Unknown"
            HealthScreen(navController, countryName)
        }
        composable("categories/visa/{countryName}") { backStackEntry ->
            val countryName = backStackEntry.arguments?.getString("countryName") ?: "Unknown"
            VisaScreen(navController, countryName)
        }
        composable("categories/security/{countryName}") { backStackEntry ->
            val countryName = backStackEntry.arguments?.getString("countryName") ?: "Unknown"
            SecurityScreen(navController, countryName)
        }
        composable("categories/news/{countryName}") { backStackEntry ->
            val countryName = backStackEntry.arguments?.getString("countryName") ?: "Unknown"
            NewsScreen(navController, countryName)
        }
        composable("categories/transport/{countryName}") { backStackEntry ->
            val countryName = backStackEntry.arguments?.getString("countryName") ?: "Unknown"
            TransportScreen(navController, countryName)
        }
        composable("write_review/{countryName}") { backStackEntry ->
            val countryName = backStackEntry.arguments?.getString("countryName") ?: ""
            WriteReviewScreen(countryName = countryName, navController = navController)
        }
        composable(NavGraph.UploadCountries.route) {
            CountryUploadScreen(viewModel = countryViewModel)
        }
        composable(NavGraph.Trips.route) {
            TripsScreen(navController = navController)
        }
        composable(NavGraph.EditProfile.route) {
            EditProfileScreen(navController = navController)
        }

        composable("country_reviews/{countryName}") { backStackEntry ->
            val countryName = backStackEntry.arguments?.getString("countryName") ?: "Unknown"
            CountryReviewsScreen(navController, countryName)
        }



    }
}

@Preview(showBackground = true)
@Composable
fun TravelShieldAppPreview() {
    TravelShieldApp()
}
