package es.uc3m.android.travelshield.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.uc3m.android.travelshield.api.WeatherApi
import es.uc3m.android.travelshield.weather.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData

    fun fetchWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val response = WeatherApi.retrofitService.getWeatherForLocation(latitude, longitude)
                if (response.isSuccessful) {
                    _weatherData.value = response.body()
                } else {
                    // Optional: log error or show fallback
                    _weatherData.value = null
                }
            } catch (e: Exception) {
                // Optional: log exception
                _weatherData.value = null
            }
        }
    }
}
