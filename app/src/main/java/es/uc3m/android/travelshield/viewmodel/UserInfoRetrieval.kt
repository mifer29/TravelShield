package es.uc3m.android.travelshield.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.graphics.Bitmap
import android.util.Log

data class UserInfo(
    val name: String = "",
    val surname: String = "",
    val profileImageUrl: String = "",
    val location: String = ""

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
                    val imageUrl = document.getString("profileImageUrl") ?: "" // si no existe aún, será ""
                    val location = document.getString("location") ?: ""

                    _userInfo.value = UserInfo(name, surname, imageUrl, location)
                }
                .addOnFailureListener {
                    // handle error if needed
                }
        }
    }
    fun updateUserInfo(name: String, surname: String, location: String? = null, imageUrl: String? = null) {
        val uid = auth.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(uid)

        val updates = mutableMapOf<String, Any>(
            "name" to name,
            "surname" to surname
        )

        if (!location.isNullOrBlank()) {
            updates["location"] = location
        }

        if (!imageUrl.isNullOrEmpty()) {
            updates["profileImageUrl"] = imageUrl
        }

        userRef.update(updates).addOnSuccessListener {
            fetchUserInfo()
        }
    }

    fun uploadProfileImageAndSaveUrl(bitmap: Bitmap) {
        val uid = auth.currentUser?.uid ?: run {
            Log.e("ProfileUpload", "No UID available. User not authenticated.")
            return
        }

        val fileName = "${uid}_profileImage"
        val storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().reference
            .child("profileImages/$fileName")

        Log.d("ProfileUpload", "Starting upload for file: profileImages/$fileName")

        viewModelScope.launch {
            try {
                val baos = java.io.ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.WEBP, 90, baos)
                val imageBytes = baos.toByteArray()
                Log.d("ProfileUpload", "Image compressed, size: ${imageBytes.size} bytes")

                val uploadTask = storageRef.putBytes(imageBytes).await()
                Log.d("ProfileUpload", "Upload successful: ${uploadTask.metadata?.path}")

                val downloadUrl = storageRef.downloadUrl.await().toString()
                Log.d("ProfileUpload", "Download URL obtained: $downloadUrl")

                firestore.collection("users").document(uid)
                    .update("profileImageUrl", downloadUrl)
                    .addOnSuccessListener {
                        Log.d("ProfileUpload", "Firestore updated with image URL.")
                        fetchUserInfo()
                    }
                    .addOnFailureListener {
                        Log.e("ProfileUpload", "Failed to update Firestore: ${it.message}", it)
                    }

            } catch (e: Exception) {
                Log.e("ProfileUpload", "Exception during image upload: ${e.message}", e)
            }
        }
    }



}
