package es.uc3m.android.travelshield.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LikeCountViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance("travelshield-db")
    private val auth = FirebaseAuth.getInstance()

    private val _likeCount = MutableStateFlow(0)
    val likeCount: StateFlow<Int> = _likeCount

    init {
        loadLikeCount()
    }

    fun loadLikeCount() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("likes")
            .document(userId)
            .collection("countries")
            .get()
            .addOnSuccessListener { snapshot ->
                _likeCount.value = snapshot.size()
            }
    }
}
