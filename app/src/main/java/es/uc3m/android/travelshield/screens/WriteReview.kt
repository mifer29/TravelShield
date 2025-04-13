package es.uc3m.android.travelshield.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
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
import es.uc3m.android.travelshield.viewmodel.Review
import es.uc3m.android.travelshield.viewmodel.UserReviewsViewModel
import kotlinx.coroutines.launch

@Composable
fun WriteReviewScreen(countryName: String, navController: NavController) {

    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0f) }
    var isSubmitting by remember { mutableStateOf(false) }
    val viewModel: UserReviewsViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Submit Review
    fun submitReview() {
        if (reviewText.isNotBlank()) {
            val review = Review(
                userId = "dummy_user_id",  // You can fetch the actual user ID from FirebaseAuth
                country = countryName,
                rating = rating.toDouble(),
                comment = reviewText,
                timestamp = System.currentTimeMillis().toString()
            )

            isSubmitting = true
            scope.launch {
                viewModel.addReview(review)
                Toast.makeText(context, "Review submitted successfully!", Toast.LENGTH_SHORT).show()
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
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Rating: ${rating.toInt()}", fontSize = 16.sp)
        Slider(
            value = rating,
            onValueChange = { rating = it },
            valueRange = 0f..5f,
            steps = 4,
            modifier = Modifier.fillMaxWidth()
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
