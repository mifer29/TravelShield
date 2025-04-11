package es.uc3m.android.travelshield.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UserInfo(
    val name: String = "",
    val surname: String = ""
)

class UserInfoRetrieval : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance("travelshield-db")
    private val auth = FirebaseAuth.getInstance()

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo

    init {
        fetchUserInfo()
    }

    private fun fetchUserInfo() {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    val name = document.getString("name") ?: ""
                    val surname = document.getString("surname") ?: ""
                    _userInfo.value = UserInfo(name, surname)
                }
                .addOnFailureListener {
                    // handle error if needed
                }
        }
    }
}
