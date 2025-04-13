package es.uc3m.android.travelshield.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import es.uc3m.android.travelshield.viewmodel.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val REVIEWS_COLLECTION = "reviews"
private const val TAG = "UserReviewsViewModel"

class UserReviewsViewModel : ViewModel() {

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> get() = _reviews

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val firestore = FirebaseFirestore.getInstance("travelshield-db")
    private val auth = FirebaseAuth.getInstance()

    init {
        fetchUserReviews()
    }

    private fun fetchUserReviews() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                _toastMessage.value = "User not authenticated."
                Log.e(TAG, "User ID is null")
                return@launch
            }

            firestore.collection(REVIEWS_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    val reviewList = result.mapNotNull { doc ->
                        doc.toObject<Review>()
                    }
                    _reviews.value = reviewList
                }
                .addOnFailureListener { exception ->
                    _toastMessage.value = "Failed to fetch reviews: ${exception.message}"
                    Log.e(TAG, "Error fetching reviews", exception)
                }
        }
    }

    // Function to add a review to Firestore
    fun addReview(review: Review) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                _toastMessage.value = "User not authenticated."
                Log.e(TAG, "User ID is null")
                return@launch
            }

            try {
                val reviewData = hashMapOf(
                    "userId" to userId,
                    "country" to review.country,
                    "rating" to review.rating,
                    "comment" to review.comment,
                    "timestamp" to review.timestamp
                )

                firestore.collection(REVIEWS_COLLECTION)
                    .add(reviewData)
                    .addOnSuccessListener {
                        _toastMessage.value = "Review added successfully!"
                        fetchUserReviews() // Refresh reviews after adding
                    }
                    .addOnFailureListener { exception ->
                        _toastMessage.value = "Failed to add review: ${exception.message}"
                        Log.e(TAG, "Error adding review", exception)
                    }
            } catch (e: Exception) {
                _toastMessage.value = "Error: ${e.message}"
                Log.e(TAG, "Exception while adding review", e)
            }
        }
    }
}
