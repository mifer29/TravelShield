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
import java.util.Date

private const val TAG = "TripViewModel"
private const val TRIPS_COLLECTION = "trips"


class TripViewModel : ViewModel() {

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> get() = _trips

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        fetchTrips()
    }

    private fun fetchTrips() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e(TAG, "User not authenticated.")
            return
        }

        firestore.collection(TRIPS_COLLECTION)
            .whereEqualTo("userId", userId)
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->
                _trips.value = result.documents.mapNotNull { it.toObject<Trip>() }
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

        val newTrip = trip.copy(userId = userId, timestamp = "today")

        firestore.collection(TRIPS_COLLECTION)
            .add(newTrip)
            .addOnSuccessListener {
                fetchTrips()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error adding trip", exception)
            }
    }
}
