package es.uc3m.android.travelshield

const val HOME_ROUTE = "home"
const val PROFILE_ROUTE = "profile"
const val SETTING_ROUTE = "settings"
const val MAP_ROUTE = "map"
const val COUNTRY_ROUTE = "country"
const val CATEGORIES_ROUTE = "categories"
const val LOGIN_ROUTE = "login"

const val GENERAL_INFO_ROUTE = "categories/general_info"
const val HEALTH_ROUTE = "categories/health"
const val NEWS_ROUTE = "categories/news"
const val SECURITY_ROUTE = "categories/security"
const val TRANSPORT_ROUTE = "categories/transport"
const val VISA_ROUTE = "categories/visa"

sealed class NavGraph(val route: String) {
    data object Home : NavGraph(HOME_ROUTE)
    data object Profile : NavGraph(PROFILE_ROUTE)
    data object Map : NavGraph(MAP_ROUTE)
    data object Country : NavGraph(COUNTRY_ROUTE)
    data object Categories : NavGraph(CATEGORIES_ROUTE)
    data object Login : NavGraph(LOGIN_ROUTE)

    data object Settings : NavGraph("$SETTING_ROUTE/{source}") {
        fun createRoute(source: String) = "$SETTING_ROUTE/$source"
    }

    // Countries categories
    data object GeneralInfo : NavGraph(GENERAL_INFO_ROUTE)
    data object Health : NavGraph(HEALTH_ROUTE)
    data object News : NavGraph(NEWS_ROUTE)
    data object Security : NavGraph(SECURITY_ROUTE)
    data object Transport : NavGraph(TRANSPORT_ROUTE)
    data object Visa : NavGraph(VISA_ROUTE)
}
