package es.uc3m.android.travelshield.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LikeViewModel(
    private val likeCountViewModel: LikeCountViewModel? = null // optional injection
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance("travelshield-db")

    private val _liked = MutableStateFlow(false)
    val liked: StateFlow<Boolean> = _liked

    fun loadLikeStatus(countryName: String) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("likes")
            .document(userId)
            .collection("countries")
            .document(countryName)
            .get()
            .addOnSuccessListener { doc ->
                _liked.value = doc.getBoolean("liked") == true
            }
    }

    fun toggleLike(countryName: String) {
        val userId = auth.currentUser?.uid ?: return
        val countryRef = firestore.collection("likes")
            .document(userId)
            .collection("countries")
            .document(countryName)

        viewModelScope.launch {
            val newValue = !_liked.value
            _liked.value = newValue
            if (newValue) {
                countryRef.set(mapOf("liked" to true))
            } else {
                countryRef.delete()
            }

            // Refresh like count after change
            likeCountViewModel?.loadLikeCount()
        }
    }

    private val _likedCountries = MutableStateFlow<List<String>>(emptyList())
    val likedCountries: StateFlow<List<String>> = _likedCountries

    fun loadLikedCountries() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("likes")
            .document(userId)
            .collection("countries")
            .get()
            .addOnSuccessListener { snapshot ->
                val names = snapshot.documents.map { it.id }
                _likedCountries.value = names
            }
    }
}
