
package es.uc3m.android.travelshield.weather

data class WeatherResponse(
    val currentWeather: CurrentWeather?
)

data class CurrentWeather(
    val temperature: Double,
    val description: String
)





