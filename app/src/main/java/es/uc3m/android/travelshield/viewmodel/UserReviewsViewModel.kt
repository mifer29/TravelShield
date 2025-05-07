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


// This viewmodel is used to fetch the user reviews in the profile
class UserReviewsViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance("travelshield-db")
    private val auth = FirebaseAuth.getInstance()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> get() = _reviews

    private val _reviewCount = MutableStateFlow(0)
    val reviewCount: StateFlow<Int> get() = _reviewCount

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _userReview = MutableStateFlow<Review?>(null)
    val userReview: StateFlow<Review?> get() = _userReview

    init {
        fetchUserReviews()
    }

    fun fetchUserReviews() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                _toastMessage.value = "User not authenticated."
                Log.e(TAG, "User ID is null")
                return@launch
            }

            try {
                val snapshot = firestore.collection(REVIEWS_COLLECTION)
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                val reviewList = snapshot.map { doc ->
                    doc.toObject<Review>().copy(reviewId = doc.id)
                }
                _reviews.value = reviewList
                _reviewCount.value = reviewList.size

            } catch (e: Exception) {
                _toastMessage.value = "Failed to fetch reviews: ${e.message}"
                Log.e(TAG, "Error fetching user reviews", e)
            }
        }
    }

    fun fetchUserReviewForCountry(userId: String, countryName: String) {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection(REVIEWS_COLLECTION)
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("country", countryName)
                    .get()
                    .await()

                val review = snapshot.documents.firstOrNull()?.toObject(Review::class.java)
                _userReview.value = review?.copy(reviewId = snapshot.documents.firstOrNull()?.id.orEmpty())

            } catch (e: Exception) {
                _userReview.value = null
                Log.e(TAG, "Error fetching user review for country: ${e.message}")
            }
        }
    }

    fun addReview(review: Review) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                _toastMessage.value = "User not authenticated."
                Log.e(TAG, "User ID is null")
                return@launch
            }

            try {
                val userDoc = firestore.collection(USERS_COLLECTION)
                    .document(userId)
                    .get()
                    .await()

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
                    .await()

                _toastMessage.value = "Review added successfully!"
                fetchUserReviews()

            } catch (e: Exception) {
                _toastMessage.value = "Failed to add review: ${e.message}"
                Log.e(TAG, "Error adding review", e)
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

                _toastMessage.value = "Review deleted successfully."
                fetchUserReviews()

            } catch (e: Exception) {
                _toastMessage.value = "Error deleting review: ${e.message}"
                Log.e(TAG, "Error deleting review", e)
            }
        }
    }

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
                    .await()

                _toastMessage.value = "Review updated successfully!"
                fetchUserReviews()

            } catch (e: Exception) {
                _toastMessage.value = "Failed to update review: ${e.message}"
                Log.e(TAG, "Error updating review", e)
            }
        }
    }

    fun fetchReviewsForUser(userId: String) {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection(REVIEWS_COLLECTION)
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                val reviewList = snapshot.map { doc ->
                    doc.toObject<Review>().copy(reviewId = doc.id)
                }
                _reviews.value = reviewList
                _reviewCount.value = reviewList.size

            } catch (e: Exception) {
                _toastMessage.value = "Failed to fetch user reviews: ${e.message}"
                Log.e(TAG, "Error fetching reviews for user", e)
            }
        }
    }
}
