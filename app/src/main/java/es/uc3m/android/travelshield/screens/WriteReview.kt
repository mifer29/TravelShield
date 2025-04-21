package es.uc3m.android.travelshield.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.uc3m.android.travelshield.viewmodel.Review
import es.uc3m.android.travelshield.viewmodel.UserReviewsViewModel
import es.uc3m.android.travelshield.notifications.NotificationHelper
import kotlinx.coroutines.launch

@Composable
fun WriteReviewScreen(countryName: String, navController: NavController) {

    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0f) }
    var isSubmitting by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("Anonymous") } // Default value
    val viewModel: UserReviewsViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val notificationHelper = NotificationHelper(context)

    // Fetch the username from Firestore
    fun fetchUserName(userId: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                // Fetch the 'name' field from Firestore
                userName = document.getString("name") ?: "Anonymous"
            } else {
                userName = "Anonymous"
            }
        }.addOnFailureListener { exception ->
            userName = "Anonymous" // Fallback to "Anonymous" on failure
            Toast.makeText(context, "Error fetching user data", Toast.LENGTH_SHORT).show()
        }
    }

    // Submit Review
    fun submitReview() {
        if (reviewText.isNotBlank()) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId == null) {
                Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
                return
            }

            // Fetch the username before submitting the review
            fetchUserName(userId)

            val review = Review(
                userId = userId,
                userName = userName, // Use the fetched userName
                country = countryName,
                rating = rating.toDouble(),
                comment = reviewText,
                timestamp = System.currentTimeMillis().toString()
            )

            isSubmitting = true
            scope.launch {
                viewModel.addReview(review)
                Toast.makeText(context, "Review submitted successfully!", Toast.LENGTH_SHORT).show()

                // Trigger the notification after successful submission
                notificationHelper.showNotification(
                    "New Review Added",
                    "You have successfully added a new review for $countryName."
                )

                isSubmitting = false
                navController.popBackStack()
            }
        } else {
            Toast.makeText(context, "Review cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())  // Add vertical scroll here
    ) {
        Text(
            text = "Write your review for $countryName",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = reviewText,
            onValueChange = { reviewText = it },
            label = { Text("Your review") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            enabled = !isSubmitting // Disable while submitting
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Rating: ${rating.toInt()}", fontSize = 16.sp)
        Slider(
            value = rating,
            onValueChange = { rating = it },
            valueRange = 0f..5f,
            steps = 4,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting // Disable while submitting
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { submitReview() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting
        ) {
            Text("Submit Review")
        }

        if (isSubmitting) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
    }
}
