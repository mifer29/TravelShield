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
import com.google.firebase.firestore.toObject

private const val COUNTRIES_COLLECTION = "countries"

class CountryViewModel : ViewModel() {
    private val _countries = MutableStateFlow<List<CountryDoc>>(emptyList())
    val countries: StateFlow<List<CountryDoc>> get() = _countries

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val firestore = FirebaseFirestore.getInstance("travelshield-db") // Custom DB name

    init {
        fetchCountries()
    }

    // Fetch countries from Firestore
    fun fetchCountries() {
        viewModelScope.launch {
            firestore.collection(COUNTRIES_COLLECTION).get()
                .addOnSuccessListener { result ->
                    val countryList = result.map { document ->
                        document.toObject<CountryDoc>().copy(id = document.id)
                    }
                    _countries.value = countryList
                }
                .addOnFailureListener { exception ->
                    _toastMessage.value = "Failed to fetch countries: ${exception.message}"
                    Log.e("CountryViewModel", "Error fetching countries", exception)
                }
        }
    }

    // Add a new country to Firestore
    fun addCountry(countryDoc: CountryDoc) {
        viewModelScope.launch {
            firestore.collection(COUNTRIES_COLLECTION)
                .add(countryDoc)
                .addOnSuccessListener {
                    fetchCountries() // Refresh the list after adding
                }
                .addOnFailureListener { exception ->
                    _toastMessage.value = "Failed to add country: ${exception.message}"
                    Log.e("CountryViewModel", "Error adding country", exception)
                }
        }
    }

    // Update an existing country in Firestore
    fun updateCountry(id: String, updatedCountry: CountryDoc) {
        viewModelScope.launch {
            firestore.collection(COUNTRIES_COLLECTION).document(id)
                .set(updatedCountry)
                .addOnSuccessListener {
                    fetchCountries() // Refresh the list after updating
                }
                .addOnFailureListener { exception ->
                    _toastMessage.value = "Failed to update country: ${exception.message}"
                    Log.e("CountryViewModel", "Error updating country", exception)
                }
        }
    }

    // Delete a country from Firestore
    fun deleteCountry(id: String) {
        viewModelScope.launch {
            firestore.collection(COUNTRIES_COLLECTION).document(id)
                .delete()
                .addOnSuccessListener {
                    fetchCountries() // Refresh the list after deleting
                }
                .addOnFailureListener { exception ->
                    _toastMessage.value = "Failed to delete country: ${exception.message}"
                    Log.e("CountryViewModel", "Error deleting country", exception)
                }
        }
    }

    // Show a toast message for UI feedback
    fun showToast(message: String?) {
        _toastMessage.value = message
    }
}
