package es.uc3m.android.travelshield.api

import es.uc3m.android.travelshield.weather.WeatherResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Weather API interface from Google Cloud
// The API key must be passed, along with the latitude and longitude of the country

interface WeatherApiService {
    @GET("v1/currentConditions:lookup")
    suspend fun getWeatherForLocation(
        @Query("location.latitude") latitude: Double,
        @Query("location.longitude") longitude: Double,
        @Query("key") apiKey: String = "AIzaSyC_deu-i-xQrqCnw1i5u2NwOwFmj_S1WsE"
    ): Response<WeatherResponse>
}

object WeatherApi {
    private const val BASE_URL = "https://weather.googleapis.com/"

    val retrofitService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}
