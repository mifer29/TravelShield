package es.uc3m.android.travelshield

import es.uc3m.android.travelshield.api.WeatherApi
import es.uc3m.android.travelshield.viewmodel.WeatherViewModel
import es.uc3m.android.travelshield.weather.WeatherResponse
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class WeatherViewModelTest {

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var mockWeatherApi: WeatherApi
    private lateinit var mockResponse: Response<WeatherResponse>

    @Before
    fun setup() {
        // Set the dispatcher for coroutines
        Dispatchers.setMain(Dispatchers.Unconfined)

        // Mock WeatherApi
        mockWeatherApi = mockk()
        mockResponse = mockk()

        // Initialize ViewModel with mocks
        weatherViewModel = WeatherViewModel()

        // Mock behavior of WeatherApi
        mockkStatic(WeatherApi::class)
        every { WeatherApi.retrofitService.getWeatherForLocation(any(), any()) } returns mockResponse
    }

    @Test
    fun `test fetchWeather success`() = runTest {
        // Given
        val weatherResponse = WeatherResponse(/* provide mock data here */)
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body() } returns weatherResponse

        // When
        weatherViewModel.fetchWeather(40.7128, -74.0060)  // Example coordinates for NYC

        // Then
        // Verify that weatherData has been updated with the response data
        assert(weatherViewModel.weatherData.value == weatherResponse)

        // Verify that the WeatherApi call was made with the correct parameters
        verify { WeatherApi.retrofitService.getWeatherForLocation(40.7128, -74.0060) }
    }

    @Test
    fun `test fetchWeather failure`() = runTest {
        // Given
        every { mockResponse.isSuccessful } returns false
        every { mockResponse.body() } returns null

        // When
        weatherViewModel.fetchWeather(40.7128, -74.0060)  // Example coordinates for NYC

        // Then
        // Verify that weatherData is null when the response is not successful
        assert(weatherViewModel.weatherData.value == null)

        // Verify that the WeatherApi call was made with the correct parameters
        verify { WeatherApi.retrofitService.getWeatherForLocation(40.7128, -74.0060) }
    }
}
