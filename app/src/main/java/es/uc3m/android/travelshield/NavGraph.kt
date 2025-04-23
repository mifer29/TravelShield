package es.uc3m.android.travelshield

const val HOME_ROUTE = "home"
const val PROFILE_ROUTE = "profile"
const val SETTING_ROUTE = "settings"
const val MAP_ROUTE = "map"
const val COUNTRY_ROUTE = "country/{countryName}"
const val CATEGORIES_ROUTE = "categories"
const val LOGIN_ROUTE = "login"
const val SIGNUP_ROUTE = "signup"
const val EDIT_PROFILE_ROUTE = "edit_profile"

const val GENERAL_INFO_ROUTE = "categories/general_info/{countryName}"
const val HEALTH_ROUTE = "categories/health/{countryName}"
const val NEWS_ROUTE = "categories/news/{countryName}"
const val SECURITY_ROUTE = "categories/security/{countryName}"
const val TRANSPORT_ROUTE = "categories/transport/{countryName}"
const val VISA_ROUTE = "categories/visa/{countryName}"
const val TRIPS_ROUTE = "trips"
const val UPLOAD_COUNTRIES_ROUTE = "upload_countries"

sealed class NavGraph(val route: String) {
    data object Home : NavGraph(HOME_ROUTE)
    data object Profile : NavGraph(PROFILE_ROUTE)
    data object Map : NavGraph(MAP_ROUTE)
    data object Country : NavGraph(COUNTRY_ROUTE)
    data object Categories : NavGraph(CATEGORIES_ROUTE)
    data object Login : NavGraph(LOGIN_ROUTE)
    data object SignUp : NavGraph(SIGNUP_ROUTE)
    data object SettingsScreen  : NavGraph(SETTING_ROUTE)
    data object Trips : NavGraph(TRIPS_ROUTE)
    // Categories with countryName as dynamic parameter
    data object GeneralInfo : NavGraph(GENERAL_INFO_ROUTE) {
        fun createRoute(countryName: String) = "categories/general_info/$countryName"
    }

    data object Health : NavGraph(HEALTH_ROUTE) {
        fun createRoute(countryName: String) = "categories/health/$countryName"
    }

    data object News : NavGraph(NEWS_ROUTE) {
        fun createRoute(countryName: String) = "categories/news/$countryName"
    }

    data object Security : NavGraph(SECURITY_ROUTE) {
        fun createRoute(countryName: String) = "categories/security/$countryName"
    }

    data object Transport : NavGraph(TRANSPORT_ROUTE) {
        fun createRoute(countryName: String) = "categories/transport/$countryName"
    }

    data object Visa : NavGraph(VISA_ROUTE) {
        fun createRoute(countryName: String) = "categories/visa/$countryName"
    }

    data object WriteReview {
        fun createRoute(countryName: String) = "write_review/$countryName"
    }

    data object UploadCountries : NavGraph(UPLOAD_COUNTRIES_ROUTE)

    data object EditProfile : NavGraph(EDIT_PROFILE_ROUTE)

}
