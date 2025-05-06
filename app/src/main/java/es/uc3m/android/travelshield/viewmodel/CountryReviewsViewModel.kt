package es.uc3m.android.travelshield.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val REVIEWS_COLLECTION = "reviews"
private const val TAG = "CountryReviewsViewModel"

class CountryReviewsViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance("travelshield-db")

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> get() = _reviews

    private var currentCountry: String? = null
    private val _isFetching = MutableStateFlow(false)
    val isFetching: StateFlow<Boolean> get() = _isFetching

    fun fetchReviewsByCountry(country: String) {
        viewModelScope.launch {
            if (_isFetching.value) {
                return@launch
            }

            _isFetching.value = true
            currentCountry = country

            try {
                val result = firestore.collection(REVIEWS_COLLECTION)
                    .whereEqualTo("country", country)
                    .get()
                    .await()

                val reviewList = result.mapNotNull { doc ->
                    doc.toObject<Review>().copy(reviewId = doc.id)
                }

                _reviews.value = reviewList

            } catch (e: Exception) {
                Log.e(TAG, "Error fetching reviews by country", e)
            } finally {
                _isFetching.value = false
            }
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            try {
                firestore.collection(REVIEWS_COLLECTION)
                    .document(reviewId)
                    .delete()
                    .await()

                currentCountry?.let { fetchReviewsByCountry(it) }

            } catch (e: Exception) {
                Log.e(TAG, "Error deleting review: ${e.message}")
            }
        }
    }
    fun fetchAllReviews() {
        viewModelScope.launch {
            try {
                val result = firestore.collection(REVIEWS_COLLECTION)
                    .get()
                    .await()

                val reviewList = result.mapNotNull { doc ->
                    doc.toObject<Review>().copy(reviewId = doc.id)
                }

                _reviews.value = reviewList

            } catch (e: Exception) {
                Log.e(TAG, "Error fetching all reviews", e)
            }
        }
    }

}
