package es.uc3m.android.travelshield.api

import es.uc3m.android.travelshield.weather.WeatherResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/weather:lookup") // Update based on your actual endpoint
    suspend fun getWeatherForLocation(
        @Query("location") location: String, // Use "latitude,longitude" format
        @Query("key") apiKey: String = "AIzaSyC_deu-i-xQrqCnw1i5u2NwOwFmj_S1WsE"
    ): Response<WeatherResponse>
}

object WeatherApi {
    private const val BASE_URL = "https://weather.googleapis.com/" // Example base URL

    val retrofitService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}
