package es.uc3m.android.travelshield

const val HOME_ROUTE = "home"
const val PROFILE_ROUTE = "profile"
const val SETTING_ROUTE = "settings"
const val MAP_ROUTE = "map"
const val COUNTRY_ROUTE = "country"
const val CATEGORIES_ROUTE = "categories"
const val LOGIN_ROUTE = "login"

sealed class NavGraph(val route: String) {
    data object Home : NavGraph(HOME_ROUTE)
    data object Profile : NavGraph(PROFILE_ROUTE)
    data object Map : NavGraph(MAP_ROUTE)
    data object Country : NavGraph(COUNTRY_ROUTE)
    data object Categories : NavGraph(CATEGORIES_ROUTE)
    data object Login: NavGraph(LOGIN_ROUTE)

    data object Settings : NavGraph("$SETTING_ROUTE/{source}") {
        // Helper function to create the route with arguments
        fun createRoute(source: String) = "$SETTING_ROUTE/$source"
    }
}