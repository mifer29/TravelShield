package es.uc3m.android.travelshield

// Define constants for navigation routes
const val HOME_ROUTE = "home"
const val PROFILE_ROUTE = "profile"
const val MAP_ROUTE = "map"
const val COUNTRY_ROUTE = "country"
const val LOGIN_ROUTE = "login"
const val SIGNUP_ROUTE = "signup"

// Category-specific routes
const val GENERAL_INFO_ROUTE = "categories/general_info"
const val HEALTH_ROUTE = "categories/health"
const val NEWS_ROUTE = "categories/news"
const val SECURITY_ROUTE = "categories/security"
const val TRANSPORT_ROUTE = "categories/transport"
const val VISA_ROUTE = "categories/visa"

// Class representing different navigation destinations
sealed class NavGraph(val route: String) {

    // Main navigation screens
    data object Home : NavGraph(HOME_ROUTE)
    data object Profile : NavGraph(PROFILE_ROUTE)
    data object Map : NavGraph(MAP_ROUTE)

    // Authentification
    data object Login: NavGraph(LOGIN_ROUTE)
    data object SignUp: NavGraph(SIGNUP_ROUTE)

    data object Country : NavGraph(COUNTRY_ROUTE)
    // Country-related category screens
    data object GeneralInfo : NavGraph(GENERAL_INFO_ROUTE)
    data object Health : NavGraph(HEALTH_ROUTE)
    data object News : NavGraph(NEWS_ROUTE)
    data object Security : NavGraph(SECURITY_ROUTE)
    data object Transport : NavGraph(TRANSPORT_ROUTE)
    data object Visa : NavGraph(VISA_ROUTE)
}
