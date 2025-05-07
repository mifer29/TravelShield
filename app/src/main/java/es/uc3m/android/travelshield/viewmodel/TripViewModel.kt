package es.uc3m.android.travelshield.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "TripViewModel"
private const val TRIPS_COLLECTION = "trips"


// Viewmodel to retrieve the trips planned
class TripViewModel : ViewModel() {

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> get() = _trips

    private val firestore = FirebaseFirestore.getInstance("travelshield-db")
    private val auth = FirebaseAuth.getInstance()

    init {
        fetchTrips() // Initial fetch when the ViewModel is created
    }

    private fun fetchTrips() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e(TAG, "User not authenticated.")
            return
        }

        firestore.collection(TRIPS_COLLECTION)
            .whereEqualTo("userId", userId) // Fetch trips for the current user
            .get()
            .addOnSuccessListener { result ->
                // Map documents to the Trip model and update the state
                val tripList = result.documents.mapNotNull { it.toObject<Trip>() }
                _trips.value = tripList
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching trips", exception)
            }
    }

    fun addTrip(trip: Trip) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e(TAG, "User not authenticated.")
            return
        }

        val newTrip = trip.copy(userId = userId)

        firestore.collection(TRIPS_COLLECTION)
            .add(newTrip)
            .addOnSuccessListener {
                // Instead of fetching trips again, just update the local state
                _trips.value = _trips.value + newTrip
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error adding trip", exception)
            }
    }

    fun fetchTripsForUser(userId: String) {
        firestore.collection("trips")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val tripList = result.documents.mapNotNull { it.toObject<Trip>() }
                _trips.value = tripList
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching trips for user", exception)
            }
    }

    fun deleteTrip(trip: Trip) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection(TRIPS_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("country", trip.country)
            .whereEqualTo("startDate", trip.startDate)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.firstOrNull()?.reference?.delete()?.addOnSuccessListener {
                    _trips.value = _trips.value.filterNot { it == trip }
                }
            }
    }

    fun updateTrip(oldTrip: Trip, newCountry: String, newStartDate: String) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection(TRIPS_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("country", oldTrip.country)
            .whereEqualTo("startDate", oldTrip.startDate)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.firstOrNull()?.reference?.update(
                    mapOf("country" to newCountry, "startDate" to newStartDate)
                )?.addOnSuccessListener {
                    fetchTrips()
                }
            }
    }
}
