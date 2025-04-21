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

    fun fetchWeather(location: String) {
        viewModelScope.launch {
            try {
                val response = WeatherApi.retrofitService.getWeatherForLocation(location)
                if (response.isSuccessful) {
                    _weatherData.value = response.body()
                } else {
                    _weatherData.value = null
                }
            } catch (e: Exception) {
                _weatherData.value = null
            }
        }
    }
}
