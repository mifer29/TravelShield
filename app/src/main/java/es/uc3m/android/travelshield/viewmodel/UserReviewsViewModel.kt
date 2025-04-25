package es.uc3m.android.travelshield.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val REVIEWS_COLLECTION = "reviews"
private const val USERS_COLLECTION = "users"
private const val TAG = "UserReviewsViewModel"

class UserReviewsViewModel : ViewModel() {

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> get() = _reviews

    private val _reviewCount = MutableStateFlow(0)
    val reviewCount: StateFlow<Int> get() = _reviewCount

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val firestore = FirebaseFirestore.getInstance("travelshield-db")
    private val auth = FirebaseAuth.getInstance()

    init {
        fetchUserReviews()
    }

    // Fetch reviews for the authenticated user
    fun fetchUserReviews() {
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
                    val reviewList = result.map { doc ->
                        doc.toObject<Review>().copy(reviewId = doc.id)
                    }
                    _reviews.value = reviewList
                    _reviewCount.value = reviewList.size
                }
                .addOnFailureListener { exception ->
                    _toastMessage.value = "Failed to fetch reviews: ${exception.message}"
                    Log.e(TAG, "Error fetching user reviews", exception)
                }
        }
    }


    // Add a review to Firestore
    fun addReview(review: Review) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                _toastMessage.value = "User not authenticated."
                Log.e(TAG, "User ID is null")
                return@launch
            }

            firestore.collection(USERS_COLLECTION).document(userId).get()
                .addOnSuccessListener { userDoc ->
                    val userName = userDoc.getString("name") ?: "Anonymous"

                    val reviewData = hashMapOf(
                        "userId" to userId,
                        "userName" to userName,
                        "country" to review.country,
                        "rating" to review.rating,
                        "comment" to review.comment,
                        "timestamp" to review.timestamp
                    )

                    firestore.collection(REVIEWS_COLLECTION)
                        .add(reviewData)
                        .addOnSuccessListener { documentRef ->
                            val reviewWithId = review.copy(reviewId = documentRef.id)
                            _toastMessage.value = "Review added successfully!"
                            fetchUserReviews()
                        }
                        .addOnFailureListener { exception ->
                            _toastMessage.value = "Failed to add review: ${exception.message}"
                            Log.e(TAG, "Error adding review", exception)
                        }
                }
                .addOnFailureListener { exception ->
                    _toastMessage.value = "Failed to fetch user name: ${exception.message}"
                    Log.e(TAG, "Error fetching user name", exception)
                }
        }
    }

    // Delete review from firebase
    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("reviews").document(reviewId).delete().await()
                fetchUserReviews()
            } catch (e: Exception) {
                _toastMessage.value = "Error deleting review: ${e.message}"
            }
        }
    }

    // Update reviews
    fun updateReview(reviewId: String, newComment: String, newRating: Double) {
        viewModelScope.launch {
            try {
                val updateData = mapOf(
                    "comment" to newComment,
                    "rating" to newRating
                )

                firestore.collection(REVIEWS_COLLECTION)
                    .document(reviewId)
                    .update(updateData)
                    .addOnSuccessListener {
                        _toastMessage.value = "Review updated successfully!"
                        fetchUserReviews() // Refresh the list
                    }
                    .addOnFailureListener { exception ->
                        _toastMessage.value = "Failed to update review: ${exception.message}"
                        Log.e(TAG, "Error updating review", exception)
                    }
            } catch (e: Exception) {
                _toastMessage.value = "Unexpected error: ${e.message}"
                Log.e(TAG, "Exception updating review", e)
            }
        }
    }
}
