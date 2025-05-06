package es.uc3m.android.travelshield.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import es.uc3m.android.travelshield.R
import es.uc3m.android.travelshield.viewmodel.Review
import es.uc3m.android.travelshield.notifications.NotificationHelper
import es.uc3m.android.travelshield.viewmodel.UserReviewsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WriteReviewScreen(countryName: String, navController: NavController) {
    val context = LocalContext.current
    val userReviewsViewModel: UserReviewsViewModel = viewModel()

    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0f) }
    var isSubmitting by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("Anonymous") }

    val userReview by userReviewsViewModel.userReview.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Fetch existing review
    LaunchedEffect(countryName) {
        userId?.let {
            userReviewsViewModel.fetchUserReviewForCountry(it, countryName)
        }
    }

    // Pre-fill if review exists
    LaunchedEffect(userReview) {
        userReview?.let {
            reviewText = it.comment
            rating = it.rating.toFloat()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(
                if (userReview != null) R.string.edit_review_for_country
                else R.string.write_review_for_country,
                countryName
            ),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = reviewText,
            onValueChange = { reviewText = it },
            label = { Text(stringResource(R.string.your_review)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            enabled = !isSubmitting
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(stringResource(R.string.rating_value, rating.toInt()), fontSize = 16.sp)

        Slider(
            value = rating,
            onValueChange = { rating = it },
            valueRange = 0f..5f,
            steps = 4,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                submitReview(
                    userId = userId,
                    userName = userName,
                    countryName = countryName,
                    reviewText = reviewText,
                    rating = rating,
                    isSubmitting = { isSubmitting = it },
                    navController = navController,
                    context = context,
                    userReviewsViewModel = userReviewsViewModel,
                    existingReviewId = userReview?.reviewId
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting
        ) {
            Text(
                stringResource(
                    if (userReview != null) R.string.update_review
                    else R.string.submit_review
                )
            )
        }

        if (isSubmitting) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
    }
}

private fun submitReview(
    userId: String?,
    userName: String,
    countryName: String,
    reviewText: String,
    rating: Float,
    isSubmitting: (Boolean) -> Unit,
    navController: NavController,
    context: android.content.Context,
    userReviewsViewModel: UserReviewsViewModel,
    existingReviewId: String?
) {
    if (reviewText.isBlank()) {
        Toast.makeText(context, context.getString(R.string.review_cannot_be_empty), Toast.LENGTH_SHORT).show()
        return
    }
    if (userId == null) {
        Toast.makeText(context, context.getString(R.string.user_not_authenticated), Toast.LENGTH_SHORT).show()
        return
    }

    val formattedTimestamp = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())

    val review = Review(
        userId = userId,
        userName = userName,
        country = countryName,
        rating = rating.toDouble(),
        comment = reviewText,
        timestamp = formattedTimestamp
    )

    isSubmitting(true)

    if (existingReviewId != null) {
        userReviewsViewModel.updateReview(
            reviewId = existingReviewId,
            newComment = review.comment,
            newRating = review.rating
        )
    } else {
        userReviewsViewModel.addReview(review)
    }

    Toast.makeText(context, context.getString(R.string.review_submitted_successfully), Toast.LENGTH_SHORT).show()
    val notificationHelper = NotificationHelper(context)
    notificationHelper.showNotification(
        "New Review Added",
        "You have successfully added a new review for $countryName."
    )
    isSubmitting(false)

    navController.navigate("country_reviews/$countryName") {
        popUpTo("country_reviews/$countryName") { inclusive = true }
    }
}
