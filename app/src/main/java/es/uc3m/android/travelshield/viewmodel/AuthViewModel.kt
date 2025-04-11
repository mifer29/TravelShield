package es.uc3m.android.travelshield.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.uc3m.android.travelshield.NavGraph
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance("travelshield-db")

    // State variables for navigation and showing error messages
    private val _route = mutableStateOf<String?>(null)
    val route: State<String?> = _route

    private val _toastMessage = mutableStateOf<String?>(null)
    val toastMessage: State<String?> = _toastMessage

    // Function for signing up a new user
    fun signUp(name: String, surname: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                val uid = auth.currentUser?.uid

                if (uid != null) {
                    val userData = hashMapOf(
                        "name" to name,
                        "surname" to surname,
                        "email" to email
                    )
                    firestore.collection("users").document(uid).set(userData).await()
                }

                _route.value = NavGraph.Home.route // Navigate to home screen after sign-up
            } catch (e: Exception) {
                _toastMessage.value = e.message
            }
        }
    }

    // Function for logging in an existing user
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _route.value = NavGraph.Home.route // Navigate to home screen after login
            } catch (e: Exception) {
                _toastMessage.value = e.message
            }
        }
    }
}
