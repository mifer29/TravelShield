
package es.uc3m.android.travelshield.weather

// Weather data model

data class WeatherResponse(
    val temperature: Temperature?,
    val feelsLikeTemperature: Temperature?,
    val weatherCondition: WeatherCondition?,
    val relativeHumidity: Int?,
    val wind: Wind?
)

data class Temperature(
    val degrees: Double,
    val unit: String
)

data class WeatherCondition(
    val description: Description?,
    val type: String
)

data class Description(
    val text: String,
    val languageCode: String
)

data class Wind(
    val direction: WindDirection?,
    val speed: WindSpeed?,
    val gust: WindSpeed?
)

data class WindDirection(
    val degrees: Int,
    val cardinal: String
)

data class WindSpeed(
    val value: Double,
    val unit: String
)








