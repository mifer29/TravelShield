package es.uc3m.android.travelshield.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.util.Log


class CountryViewModel : ViewModel() {
    // StateFlow for holding list of countries
    private val _countries = MutableStateFlow<List<CountryDoc>>(emptyList())
    val countries: StateFlow<List<CountryDoc>> = _countries

    private val _toastMessage = mutableStateOf<String?>(null)
    val toastMessage: State<String?> = _toastMessage

    private val firestore = FirebaseFirestore.getInstance()

    init {
        fetchCountries() // Fetch the countries when the ViewModel is initialized
    }

    fun fetchCountries() {
        viewModelScope.launch {
            firestore.collection("Countries")
                .get()
                .addOnSuccessListener { result ->
                    val countryList = result.map { document ->
                        document.toObject(CountryDoc::class.java).copy(id = document.id)
                    }
                    _countries.value = countryList
                    Log.d("CountryViewModel", "Countries fetched: ${_countries.value}")
                }
                .addOnFailureListener { exception ->
                    _toastMessage.value = exception.message
                    Log.e("CountryViewModel", "Error fetching countries: ${exception.message}")
                }
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
}






