package es.uc3m.android.travelshield.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import es.uc3m.android.travelshield.R
import es.uc3m.android.travelshield.viewmodel.*

@Composable
fun FindUsersScreen(navController: NavController,
                    userInfoViewModel: UserInfoRetrieval = viewModel()) {
    var nameQuery by remember { mutableStateOf("") }
    var surnameQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Pair<String, UserInfo>>>(emptyList()) }
    val userInfo by userInfoViewModel.userInfo.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(stringResource(R.string.search_users_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nameQuery,
            onValueChange = { nameQuery = it },
            label = { Text(stringResource(R.string.name_profile)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = surnameQuery,
            onValueChange = { surnameQuery = it },
            label = { Text(stringResource(R.string.surname_profile)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Button(onClick = {
                if (nameQuery.isNotBlank() && surnameQuery.isNotBlank()) {
                    FirebaseFirestore.getInstance("travelshield-db").collection("users")
                        .whereEqualTo("name", nameQuery)
                        .whereEqualTo("surname", surnameQuery)
                        .get()
                        .addOnSuccessListener { result ->
                            searchResults = result.documents.mapNotNull { doc ->
                                val user = doc.toObject(UserInfo::class.java)
                                if (user != null) doc.id to user else null
                            }
                        }
                }
            }) {
                Text(stringResource(R.string.search_users))
            }
            Button(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.go_back))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        searchResults.forEach { (userId, user) ->
            UserCard(userId = userId, user = user)
        }
    }
}

@Composable
fun UserCard(userId: String, user: UserInfo) {
    val likeCountViewModel: LikeCountViewModel = viewModel()
    val tripViewModel: TripViewModel = viewModel()
    val userReviewsViewModel: UserReviewsViewModel = viewModel()

    val likeCount by likeCountViewModel.likeCount.collectAsState()
    val trips by tripViewModel.trips.collectAsState()
    val reviews by userReviewsViewModel.reviews.collectAsState()
    val reviewCount by userReviewsViewModel.reviewCount.collectAsState()

    LaunchedEffect(userId) {
        likeCountViewModel.loadLikeCountForUser(userId)
        tripViewModel.fetchTripsForUser(userId)
        userReviewsViewModel.fetchReviewsForUser(userId)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${user.name} ${user.surname}",
                style = MaterialTheme.typography.headlineSmall
            )

            if (user.location.isNotBlank()) {
                Text(user.location, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            } else {
                Text(stringResource(R.string.no_location), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat(reviewCount.toString(), stringResource(R.string.reviews))
                ProfileStat(trips.size.toString(), stringResource(R.string.future_travels))
                ProfileStat(likeCount.toString(), stringResource(R.string.likes_given))
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (reviews.isNotEmpty()) {
                Text(stringResource(R.string.reviews), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                reviews.forEach { review ->
                    Text(
                        text = "${review.country}: ${review.comment.take(60)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
